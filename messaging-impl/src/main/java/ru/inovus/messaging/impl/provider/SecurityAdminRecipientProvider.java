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
import ru.inovus.messaging.api.model.ProviderRecipient;
import ru.inovus.messaging.impl.RecipientProvider;

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
        restUserCriteria.setPageNumber(criteria.getPageNumber());
        restUserCriteria.setPageSize(criteria.getPageSize());
        restUserCriteria.setFio(criteria.getName());
        Page<net.n2oapp.security.admin.api.model.User> userPage = userRestService.findAll(restUserCriteria);
        return userPage.getContent().isEmpty() ? Page.empty() :
                new PageImpl<>(userPage.getContent().stream().map(this::mapSecurityUser).collect(Collectors.toList()), userPage.getPageable(), userPage.getTotalElements());
    }

    public Page<Role> getRoles(SecurityBaseCriteria criteria) {
        RestRoleCriteria restRoleCriteria = new RestRoleCriteria();
        restRoleCriteria.setName(criteria.getName());
        return roleRestService.findAll(restRoleCriteria);
    }

    public Page<Region> getRegions(SecurityBaseCriteria criteria) {
        RestRegionCriteria restRegionCriteria = new RestRegionCriteria();
        restRegionCriteria.setName(criteria.getName());
        return regionRestService.getAll(restRegionCriteria);
    }

    public Page<Organization> getMedOrganizations(SecurityBaseCriteria criteria) {
        RestOrganizationCriteria restRegionCriteria = new RestOrganizationCriteria();
        restRegionCriteria.setName(criteria.getName());
        return organizationRestService.getAll(restRegionCriteria);
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