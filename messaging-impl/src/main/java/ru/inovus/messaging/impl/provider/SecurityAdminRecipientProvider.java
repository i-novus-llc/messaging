package ru.inovus.messaging.impl.provider;

import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.model.Region;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.rest.api.OrganizationRestService;
import net.n2oapp.security.admin.rest.api.RegionRestService;
import net.n2oapp.security.admin.rest.api.RoleRestService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestOrganizationCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestRegionCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestRoleCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.inovus.messaging.api.criteria.ProviderRecipientCriteria;
import ru.inovus.messaging.api.criteria.SecurityBaseCriteria;
import ru.inovus.messaging.api.model.BaseResponse;
import ru.inovus.messaging.api.model.ProviderRecipient;
import ru.inovus.messaging.impl.RecipientProvider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Провайдер для работы с получателями уведомлений из security-admin
 */
public class SecurityAdminRecipientProvider implements RecipientProvider {

    private final UserRestService userRestService;
    private final RoleRestService roleRestService;
    private final RegionRestService regionRestService;
    private final OrganizationRestService organizationRestService;

    public SecurityAdminRecipientProvider(UserRestService userRestService, RoleRestService roleRestService,
                                          RegionRestService regionRestService, OrganizationRestService organizationRestService) {
        this.userRestService = userRestService;
        this.roleRestService = roleRestService;
        this.regionRestService = regionRestService;
        this.organizationRestService = organizationRestService;
    }

    public Page<ProviderRecipient> getRecipients(ProviderRecipientCriteria criteria) {
        RestUserCriteria restUserCriteria = new RestUserCriteria();
        restUserCriteria.setUsername(criteria.getUsername());
        restUserCriteria.setPage(criteria.getPageNumber());
        restUserCriteria.setSize(criteria.getPageSize());
        restUserCriteria.setFio(criteria.getName());
        restUserCriteria.setRoleCodes(criteria.getRoleCodes());
        restUserCriteria.setRegionId(criteria.getRegionId());
        restUserCriteria.setOrganizationId(criteria.getOrganizationId());
        Page<net.n2oapp.security.admin.api.model.User> userPage = userRestService.findAll(restUserCriteria);
        return userPage.getContent().isEmpty() ? Page.empty() :
                new PageImpl<>(userPage.getContent().stream().map(this::mapSecurityUser).collect(Collectors.toList()), userPage.getPageable(), userPage.getTotalElements());
    }

    public Page<Role> getRoles(SecurityBaseCriteria criteria) {
        RestRoleCriteria restRoleCriteria = new RestRoleCriteria();
        restRoleCriteria.setPage(criteria.getPageNumber());
        restRoleCriteria.setSize(criteria.getPageSize());
        restRoleCriteria.setName(criteria.getName());
        return roleRestService.findAll(restRoleCriteria);
    }

    public Page<Region> getRegions(SecurityBaseCriteria criteria) {
        RestRegionCriteria restRegionCriteria = new RestRegionCriteria();
        restRegionCriteria.setPage(criteria.getPageNumber());
        restRegionCriteria.setSize(criteria.getPageSize());
        restRegionCriteria.setName(criteria.getName());
        return regionRestService.getAll(restRegionCriteria);
    }

    public Page<Organization> getMedOrganizations(SecurityBaseCriteria criteria) {
        RestOrganizationCriteria restRegionCriteria = new RestOrganizationCriteria();
        restRegionCriteria.setPage(criteria.getPageNumber());
        restRegionCriteria.setSize(criteria.getPageSize());
        restRegionCriteria.setName(criteria.getName());
        return organizationRestService.getAll(restRegionCriteria);
    }

    public List<BaseResponse> getRoles(String joinedRoles) {
        String[] roles = joinedRoles.split(", ");
        if (roles.length == 0) return null;

        return Arrays.stream(roles)
                .map(role -> roleRestService.getById(Integer.valueOf(role)))
                .map(role -> new BaseResponse(role.getId(), role.getName()))
                .collect(Collectors.toList());
    }

    public BaseResponse getRegion(String name) {
        SecurityBaseCriteria criteria = new SecurityBaseCriteria();
        criteria.setName(name);
        return getRegions(criteria).getContent().stream()
                .map(region -> new BaseResponse(region.getId(), region.getName())).findFirst().orElse(null);
    }

    public BaseResponse getMedOrganization(Integer id) {
        Organization organization = organizationRestService.get(id);
        return new BaseResponse(organization.getId(), organization.getFullName());
    }

    private ProviderRecipient mapSecurityUser(net.n2oapp.security.admin.api.model.User securityUser) {
        ProviderRecipient recipient = new ProviderRecipient();
        recipient.setUsername(securityUser.getUsername());
        recipient.setFio(securityUser.getFio());
        recipient.setEmail(securityUser.getEmail());
        recipient.setSurname(securityUser.getSurname());
        recipient.setName(securityUser.getName());
        recipient.setPatronymic(securityUser.getPatronymic());

        return recipient;
    }
}