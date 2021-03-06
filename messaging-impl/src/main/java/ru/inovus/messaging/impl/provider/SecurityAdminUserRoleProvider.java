package ru.inovus.messaging.impl.provider;

import net.n2oapp.security.admin.rest.api.RoleRestService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestRoleCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.CollectionUtils;
import ru.inovus.messaging.api.criteria.RoleCriteria;
import ru.inovus.messaging.api.criteria.UserCriteria;
import ru.inovus.messaging.api.model.Role;
import ru.inovus.messaging.api.model.User;
import ru.inovus.messaging.impl.UserRoleProvider;

import java.util.Collections;
import java.util.stream.Collectors;

public class SecurityAdminUserRoleProvider implements UserRoleProvider {

    private UserRestService userRestService;

    private RoleRestService roleRestService;

    public SecurityAdminUserRoleProvider(@Qualifier("userRestServiceJaxRsProxyClient") UserRestService userRestService,
                                         @Qualifier("roleRestServiceJaxRsProxyClient") RoleRestService roleRestService) {
        this.userRestService = userRestService;
        this.roleRestService = roleRestService;
    }

    @Override
    public Page<Role> getRoles(RoleCriteria criteria) {
        RestRoleCriteria restRoleCriteria = new RestRoleCriteria();
        restRoleCriteria.setPageNumber(criteria.getPageNumber());
        restRoleCriteria.setPageSize(criteria.getPageSize());
        restRoleCriteria.setName(criteria.getName());
        restRoleCriteria.setPermissionCodes(criteria.getPermissionCodes());
        Page<net.n2oapp.security.admin.api.model.Role> rolePage = roleRestService.findAll(restRoleCriteria);
        return rolePage.getContent().isEmpty() ? Page.empty() :
                new PageImpl<>(rolePage.getContent().stream().map(this::mapSecurityRole).collect(Collectors.toList()), rolePage.getPageable(), rolePage.getTotalElements());
    }

    public Page<User> getUsers(UserCriteria criteria) {
        RestUserCriteria restUserCriteria = new RestUserCriteria();
        restUserCriteria.setUsername(criteria.getUsername());
        restUserCriteria.setPageNumber(criteria.getPageNumber());
        restUserCriteria.setPageSize(criteria.getPageSize());
        restUserCriteria.setFio(criteria.getName());
        Page<net.n2oapp.security.admin.api.model.User> userPage = userRestService.findAll(restUserCriteria);
        return userPage.getContent().isEmpty() ? Page.empty() :
                new PageImpl<>(userPage.getContent().stream().map(this::mapSecurityUser).collect(Collectors.toList()), userPage.getPageable(), userPage.getTotalElements());
    }

    private Role mapSecurityRole(net.n2oapp.security.admin.api.model.Role securityRole) {
        Role role = new Role();
        role.setId(securityRole.getId().toString());
        role.setName(securityRole.getName());
        role.setCode(securityRole.getCode());
        role.setDescription(securityRole.getDescription());
        return role;
    }

    private User mapSecurityUser(net.n2oapp.security.admin.api.model.User securityUser) {
        User user = new User();
        user.setUsername(securityUser.getUsername());
        user.setFio(securityUser.getFio());
        user.setEmail(securityUser.getEmail());
        user.setSurname(securityUser.getSurname());
        user.setName(securityUser.getName());
        user.setPatronymic(securityUser.getPatronymic());
        user.setRoles(CollectionUtils.isEmpty(securityUser.getRoles()) ? Collections.emptyList() :
                securityUser.getRoles().stream().map(this::mapSecurityRole).collect(Collectors.toList()));

        return user;
    }
}