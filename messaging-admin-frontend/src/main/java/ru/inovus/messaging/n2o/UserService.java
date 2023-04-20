package ru.inovus.messaging.n2o;

import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.auth.common.KeycloakUserService;
import net.n2oapp.security.auth.common.UserAttributeKeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.n2oapp.security.auth.common.UserParamsUtil.extractFromMap;

public class UserService extends KeycloakUserService {

    private final UserAttributeKeys userAttributeKeys;
    private List<String> principalKeys;

    public UserService(UserAttributeKeys userAttributeKeys, UserDetailsService userDetailsService, String externalSystem) {
        super(userAttributeKeys, userDetailsService, externalSystem);
        this.userAttributeKeys = userAttributeKeys;
        principalKeys = userAttributeKeys.principal;
    }

    @Override
    protected User getUser(Map<String, Object> map) {
        Object usernameObj = extractFromMap(principalKeys, map);
        if (usernameObj == null)
            return null;

        User user = new User();
        user.setUsername((String) usernameObj);

        Object roles = extractFromMap(userAttributeKeys.authorities, map);
        List<String> roleList = new ArrayList<>();
        if (roles instanceof Collection) {
            roleList = new ArrayList<>((Collection<String>) roles);

        }
        user.setRoles(roleList.stream().map(r -> {
            Role role = new Role();
            role.setCode(r);
            role.setName(r);
            return role;
        }).collect(Collectors.toList()));

        user.setSurname((String) extractFromMap(userAttributeKeys.surname, map));
        user.setName((String) extractFromMap(userAttributeKeys.name, map));
        user.setEmail((String) extractFromMap(userAttributeKeys.email, map));
        user.setPatronymic((String) extractFromMap(userAttributeKeys.patronymic, map));

        //todo 7.x.x security backend. Enrich user and synchronize with security-admin backend
        return user;
    }
}
