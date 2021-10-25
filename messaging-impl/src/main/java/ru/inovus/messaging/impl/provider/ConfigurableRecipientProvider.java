package ru.inovus.messaging.impl.provider;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import ru.inovus.messaging.api.criteria.ProviderRecipientCriteria;
import ru.inovus.messaging.api.model.ProviderRecipient;
import ru.inovus.messaging.impl.RecipientProvider;
import ru.inovus.messaging.impl.provider.xml.XmlMapping;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ConfigurableRecipientProvider implements RecipientProvider {

    private static Logger logger = LoggerFactory.getLogger(ConfigurableRecipientProvider.class);

    @Setter
    private RestTemplate restTemplate;

    @Autowired
    private HttpServletRequest request;

    private ResourceLoader resourceLoader;

    private String mappingFileLocation;

    private String userUrl;

    private String[] userResponseContentLocation;

    private Map<String, String> userMapping = new HashMap<>();

    private Map<String, String> userCriteriaMapping = new HashMap<>();

    public ConfigurableRecipientProvider(ResourceLoader resourceLoader, String mappingFileLocation, String userUrl) throws IOException, JAXBException {
        this.resourceLoader = resourceLoader;
        this.mappingFileLocation = mappingFileLocation;
        this.userUrl = userUrl;
        init();
    }

    private void init() throws IOException, JAXBException {
        restTemplate = new RestTemplate();
        XmlMapping mapping = readXml();

        if (nonNull(mapping.recipientMapping)) {
            userResponseContentLocation = mapping.recipientMapping.contentMapping.split("\\.");
            userMapping.put("count", mapping.recipientMapping.countMapping);
            if (nonNull(mapping.recipientMapping.response))
                mapping.recipientMapping.response.fields.forEach(field -> userMapping.put(field.getTagName(), field.getAttribute("mapping")));
            if (nonNull(mapping.recipientMapping.criteria))
                mapping.recipientMapping.criteria.fields.forEach(field -> userCriteriaMapping.put(field.getTagName(), field.getAttribute("mapping")));
        }
    }

    @Override
    public Page<ProviderRecipient> getRecipients(ProviderRecipientCriteria criteria) {
        Map<String, Object> response = restTemplate.exchange(userUrl + "?" + buildQueryParam(criteria),
                HttpMethod.GET, prepareRequest(request), Map.class).getBody();
        Page page = new PageImpl(mapUsers(response), criteria, (Integer) response.get(userMapping.get("count")));
        return page;
    }

    private HttpEntity<Map<String, Object>> prepareRequest(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        if (nonNull(request)) headers.set("Authorization", request.getHeader("Authorization"));
        return new HttpEntity<>(headers);
    }

    private List<ProviderRecipient> mapUsers(Map<String, Object> response) {
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
        List<ProviderRecipient> result = new ArrayList<>();
        for (Map<String, Object> responseUser : (List<Map<String, Object>>) content) {
            ProviderRecipient user = new ProviderRecipient();
            if (userMapping.containsKey("username"))
                user.setUsername((String) responseUser.get(userMapping.get("username")));
            if (userMapping.containsKey("fio")) user.setFio((String) responseUser.get(userMapping.get("fio")));
            if (userMapping.containsKey("email")) user.setEmail((String) responseUser.get(userMapping.get("email")));
            if (userMapping.containsKey("surname"))
                user.setSurname((String) responseUser.get(userMapping.get("surname")));
            if (userMapping.containsKey("name")) user.setName((String) responseUser.get(userMapping.get("name")));
            if (userMapping.containsKey("patronymic"))
                user.setPatronymic((String) responseUser.get(userMapping.get("patronymic")));
            result.add(user);
        }
        return result;
    }

    private String buildQueryParam(ProviderRecipientCriteria criteria) {
        Map<String, Object> params = new HashMap<>();
        if (userCriteriaMapping.containsKey("username"))
            params.put(userCriteriaMapping.get("username"), criteria.getUsername());
        if (userCriteriaMapping.containsKey("name")) params.put(userCriteriaMapping.get("name"), criteria.getName());
        if (userCriteriaMapping.containsKey("fio")) params.put(userCriteriaMapping.get("fio"), criteria.getFio());
        if (userCriteriaMapping.containsKey("page-size"))
            params.put(userCriteriaMapping.get("page-size"), criteria.getPageSize());
        if (userCriteriaMapping.containsKey("page-number"))
            params.put(userCriteriaMapping.get("page-number"), criteria.getPageNumber());
        return buildQueryParam(params);
    }

    private String buildQueryParam(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (isNull(entry.getValue())) continue;
            if (sb.length() > 0)
                sb.append("&");
            if (!(entry.getValue() instanceof Iterable))
                sb.append(String.format("%s=%s",
                        entry.getKey(),
                        entry.getValue().toString()
                ));
            else if (entry.getValue() instanceof Iterable) {
                Iterator iterator = ((Iterable) entry.getValue()).iterator();
                if (!iterator.hasNext()) sb.deleteCharAt(sb.length() - 1);
                while (iterator.hasNext()) {
                    Object next = iterator.next();
                    if (isNull(next)) continue;
                    sb.append(String.format("%s=%s",
                            entry.getKey(),
                            next.toString()));
                    if (iterator.hasNext())
                        sb.append("&");
                }
            }
        }
        return sb.toString();
    }

    private XmlMapping readXml() throws IOException, JAXBException {
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
