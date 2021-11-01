package ru.inovus.messaging.impl.provider;

import net.n2oapp.security.admin.rest.api.UserRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.inovus.messaging.api.criteria.ProviderRecipientCriteria;
import ru.inovus.messaging.api.model.ProviderRecipient;
import ru.inovus.messaging.impl.RecipientProvider;

import java.util.stream.Collectors;

/**
 * Провайдер для работы с получателями уведомлений из security-admin
 */
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