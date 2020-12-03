package ru.inovus.messaging.impl;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Element;
import ru.inovus.messaging.api.criteria.RoleCriteria;
import ru.inovus.messaging.api.criteria.UserCriteria;
import ru.inovus.messaging.api.model.Role;
import ru.inovus.messaging.api.model.User;
import ru.inovus.messaging.impl.xml.XmlMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ConfigurableUserRoleProvider implements UserRoleProvider {

    private static Logger logger = LoggerFactory.getLogger(ConfigurableUserRoleProvider.class);

    private RestTemplate restTemplate;

    @Value("${messaging.user-provider-url}")
    private String userUrl;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    ResourceLoader resourceLoader;

    @Value("${messaging.mapping-file-location}")
    private String mappingFileLocation;

    @Value("${messaging.role-provider-url}")
    private String roleUrl;

    private String[] userResponseContentLocation;

    private String[] roleResponseContentLocation;

    private Map<String, String> userMapping = new HashMap<>();

    private Map<String, String> userCriteriaMapping = new HashMap<>();

    private Boolean isUserRolePlainStringArray;

    private Map<String, String> userRoleMapping = null;

    private Map<String, String> roleMapping = new HashMap<>();

    private Map<String, String> roleCriteriaMapping = new HashMap<>();

    @PostConstruct
    private void init() {
        restTemplate = new RestTemplate();
        XmlMapping mapping = readXml();
        if (nonNull(mapping.roleMapping)) {
            roleResponseContentLocation = mapping.roleMapping.contentMapping.split("\\.");
            roleMapping.put("count", mapping.roleMapping.countMapping);
            if (nonNull(mapping.roleMapping.response))
                mapping.roleMapping.response.fields.forEach(field -> roleMapping.put(field.getTagName(), field.getAttribute("response-mapping")));
            if (nonNull(mapping.roleMapping.criteria))
                mapping.roleMapping.criteria.fields.forEach(field -> roleCriteriaMapping.put(field.getTagName(), field.getAttribute("query-mapping")));
        }

        Element roleXmlElement = null;
        if (nonNull(mapping.userMapping)) {
            userResponseContentLocation = mapping.userMapping.contentMapping.split("\\.");
            userMapping.put("count", mapping.userMapping.countMapping);
            if (nonNull(mapping.userMapping.response)) {
                mapping.userMapping.response.fields.forEach(field -> userMapping.put(field.getTagName(), field.getAttribute("response-mapping")));
                roleXmlElement = mapping.userMapping.response.fields.stream().filter(field -> field.getTagName().equals("roles")).findFirst().orElse(null);
            }
            if (nonNull(mapping.userMapping.criteria))
                mapping.userMapping.criteria.fields.forEach(field -> userCriteriaMapping.put(field.getTagName(), field.getAttribute("query-mapping")));
        }

        isUserRolePlainStringArray = Boolean.valueOf(isNull(roleXmlElement) ? "false" : roleXmlElement.getAttribute("plain-string-array"));
        if (nonNull(roleXmlElement) && roleXmlElement.getAttributes().getLength() > 2 && !isUserRolePlainStringArray) {
            userRoleMapping = new HashMap<>();
            if (roleXmlElement.hasAttribute("id-mapping")) userRoleMapping.put("id", roleXmlElement.getAttribute("id-mapping"));
            if (roleXmlElement.hasAttribute("name-mapping")) userRoleMapping.put("name", roleXmlElement.getAttribute("name-mapping"));
            if (roleXmlElement.hasAttribute("code-mapping")) userRoleMapping.put("code", roleXmlElement.getAttribute("code-mapping"));
            if (roleXmlElement.hasAttribute("description-mapping")) userRoleMapping.put("description", roleXmlElement.getAttribute("description-mapping"));
        }
    }

    @Override
    public Page<Role> getRoles(RoleCriteria criteria) {
        Map<String, Object> response = restTemplate.exchange(roleUrl + "?" + buildQueryParam(criteria),
                HttpMethod.GET, prepareRequest(request.getHeader("Authorization")), Map.class).getBody();
        Page page = new PageImpl(mapRoles(response), criteria, (Integer) response.get(roleMapping.get("count")));
        return page;
    }

    @Override
    public Page<User> getUsers(UserCriteria criteria) {
        Map<String, Object> response = restTemplate.exchange(userUrl + "?" + buildQueryParam(criteria),
                HttpMethod.GET, prepareRequest(request.getHeader("Authorization")), Map.class).getBody();
        Page page = new PageImpl(mapUsers(response), criteria, (Integer) response.get(userMapping.get("count")));
        return page;
    }

    private HttpEntity prepareRequest(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        return new HttpEntity<>(headers);
    }

    private List<User> mapUsers(Map<String, Object> response) {
        Object content = null;
        if (userResponseContentLocation.length > 1)
            for (int i = 0; i < userResponseContentLocation.length; i++) {
                if (i == userResponseContentLocation.length - 1)
                    content = ((Map<String, Object>) content).get(userResponseContentLocation[userResponseContentLocation.length - 1]);
                else
                    content = response.get(userResponseContentLocation[i]);
            }
        else
            content = response.get(userResponseContentLocation[0]);
        List<User> result = new ArrayList<>();
        for (Map<String, Object> responseUser : (List<Map<String, Object>>) content) {
            User user = new User();
            if (userMapping.containsKey("username")) user.setUsername((String) responseUser.get(userMapping.get("username")));
            if (userMapping.containsKey("fio")) user.setFio((String) responseUser.get(userMapping.get("fio")));
            if (userMapping.containsKey("email")) user.setEmail((String) responseUser.get(userMapping.get("email")));
            if (userMapping.containsKey("surname")) user.setSurname((String) responseUser.get(userMapping.get("surname")));
            if (userMapping.containsKey("name")) user.setName((String) responseUser.get(userMapping.get("name")));
            if (userMapping.containsKey("patronymic")) user.setPatronymic((String) responseUser.get(userMapping.get("patronymic")));
            if (userMapping.containsKey("roles")) user.setRoles(mapUsersRoles((List) responseUser.get(userMapping.get("roles"))));
            result.add(user);
        }
        return result;
    }

    private List<Role> mapUsersRoles(List roles) {
        if (nonNull(roles)) {
            List<Role> roleList = null;
            if (isUserRolePlainStringArray)
                roleList = ((List<String>) roles).stream().map(this::roleFromString).collect(Collectors.toList());

            if (isNull(roleList))
                roleList = ((List<Map>) roles).stream().map(this::mapRoleFromUser).collect(Collectors.toList());
            return roleList;
        }
        return Collections.emptyList();
    }

    private Role mapRoleFromUser(Map<String, String> role) {
        Role roleModel = new Role();
        Map<String, String> currentMapping = nonNull(userRoleMapping) ? userRoleMapping : roleMapping;
        if (currentMapping.containsKey("id")) roleModel.setId(currentMapping.get("id"));
        if (currentMapping.containsKey("name")) roleModel.setName(currentMapping.get("name"));
        if (currentMapping.containsKey("code")) roleModel.setCode(currentMapping.get("code"));
        if (currentMapping.containsKey("description")) roleModel.setDescription(currentMapping.get("description"));
        return roleModel;
    }

    private List<Role> mapRoles(Map<String, Object> response) {
        Object content = null;
        if (roleResponseContentLocation.length > 1)
            for (int i = 0; i < roleResponseContentLocation.length; i++) {
                if (i == roleResponseContentLocation.length - 1)
                    content = ((Map<String, Object>) content).get(roleResponseContentLocation[roleResponseContentLocation.length - 1]);
                else
                    content = response.get(roleResponseContentLocation[i]);
            }
        else
            content = response.get(roleResponseContentLocation[0]);

        List<Role> result = new ArrayList<>();
        for (Map<String, Object> responseRole : (List<Map<String, Object>>) content) {
            Role role = new Role();
            if (roleMapping.containsKey("id")) role.setId((String) responseRole.get(roleMapping.get("id")));
            if (roleMapping.containsKey("name")) role.setName((String) responseRole.get(roleMapping.get("name")));
            if (roleMapping.containsKey("code")) role.setCode((String) responseRole.get(roleMapping.get("code")));
            if (roleMapping.containsKey("description")) role.setDescription((String) responseRole.get(roleMapping.get("description")));
            result.add(role);
        }
        return result;
    }

    private String buildQueryParam(RoleCriteria criteria) {
        Map<String, Object> params = new HashMap<>();
        if (roleCriteriaMapping.containsKey("name")) params.put(roleCriteriaMapping.get("name"), criteria.getName());
        if (roleCriteriaMapping.containsKey("permissionCodes")) params.put(roleCriteriaMapping.get("permissionCodes"), criteria.getPermissionCodes());
        if (roleCriteriaMapping.containsKey("page-size")) params.put(roleCriteriaMapping.get("page-size"), criteria.getPageSize());
        if (roleCriteriaMapping.containsKey("page-number")) params.put(roleCriteriaMapping.get("page-number"), criteria.getPageNumber());
        return urlEncodeUTF8(params);
    }

    private String buildQueryParam(UserCriteria criteria) {
        Map<String, Object> params = new HashMap<>();
        if (userCriteriaMapping.containsKey("username")) params.put(userCriteriaMapping.get("username"), criteria.getUsername());
        if (userCriteriaMapping.containsKey("name")) params.put(userCriteriaMapping.get("name"), criteria.getName());
        if (userCriteriaMapping.containsKey("fio")) params.put(userCriteriaMapping.get("fio"), criteria.getFio());
        if (userCriteriaMapping.containsKey("roleIds")) params.put(userCriteriaMapping.get("roleIds"), criteria.getRoleIds());
        if (userCriteriaMapping.containsKey("page-size")) params.put(userCriteriaMapping.get("page-size"), criteria.getPageSize());
        if (userCriteriaMapping.containsKey("page-number")) params.put(userCriteriaMapping.get("page-number"), criteria.getPageNumber());
        return urlEncodeUTF8(params);
    }

    private String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private String urlEncodeUTF8(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (isNull(entry.getValue())) continue;
            if (sb.length() > 0)
                sb.append("&");
            if (!(entry.getValue() instanceof Iterable))
                sb.append(String.format("%s=%s",
                        urlEncodeUTF8(entry.getKey()),
                        urlEncodeUTF8(entry.getValue().toString())
                ));
            else if (entry.getValue() instanceof Iterable) {
                Iterator iterator = ((Iterable) entry.getValue()).iterator();
                if (!iterator.hasNext()) sb.deleteCharAt(sb.length() - 1);
                while (iterator.hasNext()) {
                    Object next = iterator.next();
                    if (isNull(next)) continue;
                    sb.append(String.format("%s=%s",
                            urlEncodeUTF8(entry.getKey()),
                            urlEncodeUTF8(next.toString())));
                    if (iterator.hasNext())
                        sb.append("&");
                }
            }
        }
        return sb.toString();
    }

    private Role roleFromString(String s) {
        Role role = new Role();
        role.setCode(s);
        role.setName(s);
        return role;
    }

    @SneakyThrows
    private XmlMapping readXml() {
        XmlMapping mapping;
        try {
            InputStream io = resourceLoader.getResource(mappingFileLocation).getInputStream();
            Unmarshaller unmarshaller = XmlMapping.JAXB_CONTEXT.createUnmarshaller();
            mapping = (XmlMapping) unmarshaller.unmarshal(io);
        } catch (JAXBException | IOException e) {
            logger.error("xml mapping file load error ", e);
            throw e;
        }
        return mapping;
    }
}
