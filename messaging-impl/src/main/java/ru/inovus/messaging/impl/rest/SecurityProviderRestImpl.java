package ru.inovus.messaging.impl.rest;

import lombok.RequiredArgsConstructor;
import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.model.Region;
import net.n2oapp.security.admin.api.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.criteria.SecurityBaseCriteria;
import ru.inovus.messaging.api.rest.SecurityProviderRest;
import ru.inovus.messaging.impl.provider.SecurityAdminRecipientProvider;

@Controller
@RequiredArgsConstructor
public class SecurityProviderRestImpl implements SecurityProviderRest {

    private final SecurityAdminRecipientProvider provider;

    @Override
    public Page<Role> getRoles(SecurityBaseCriteria criteria) {
        return provider.getRoles(criteria);
    }

    @Override
    public Page<Region> getRegions(SecurityBaseCriteria criteria) {
        return provider.getRegions(criteria);
    }

    @Override
    public Page<Organization> getMedOrganizations(SecurityBaseCriteria criteria) {
        return provider.getMedOrganizations(criteria);
    }
}
