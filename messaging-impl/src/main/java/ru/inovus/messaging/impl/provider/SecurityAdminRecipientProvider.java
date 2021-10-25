package ru.inovus.messaging.impl.provider;

import net.n2oapp.security.admin.rest.api.UserRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.inovus.messaging.api.criteria.ProviderRecipientCriteria;
import ru.inovus.messaging.api.model.ProviderRecipient;
import ru.inovus.messaging.impl.RecipientProvider;

import java.util.stream.Collectors;

public class SecurityAdminRecipientProvider implements RecipientProvider {

    private final UserRestService userRestService;

    public SecurityAdminRecipientProvider(UserRestService userRestService) {
        this.userRestService = userRestService;
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

    private ProviderRecipient mapSecurityUser(net.n2oapp.security.admin.api.model.User securityUser) {
        ProviderRecipient user = new ProviderRecipient();
        user.setUsername(securityUser.getUsername());
        user.setFio(securityUser.getFio());
        user.setEmail(securityUser.getEmail());
        user.setSurname(securityUser.getSurname());
        user.setName(securityUser.getName());
        user.setPatronymic(securityUser.getPatronymic());

        return user;
    }
}