package ru.inovus.messaging.impl.rest;

import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.model.Region;
import net.n2oapp.security.admin.api.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.criteria.SecurityBaseCriteria;
import ru.inovus.messaging.api.rest.SecurityProviderRest;
import ru.inovus.messaging.impl.provider.SecurityAdminRecipientProvider;

import static java.util.Objects.isNull;

@Controller
public class SecurityProviderRestImpl implements SecurityProviderRest {

    @Autowired(required = false)
    private SecurityAdminRecipientProvider provider;

    @Override
    public Page<Role> getRoles(SecurityBaseCriteria criteria) {
        checkProvider();
        return provider.getRoles(criteria);
    }

    @Override
    public Page<Region> getRegions(SecurityBaseCriteria criteria) {
        checkProvider();
        return provider.getRegions(criteria);
    }

    @Override
    public Page<Organization> getMedOrganizations(SecurityBaseCriteria criteria) {
        checkProvider();
        return provider.getMedOrganizations(criteria);
    }

    private void checkProvider() {
        if (isNull(provider)) throw new UnsupportedOperationException("Supported only with 'security' provider");
    }
}
