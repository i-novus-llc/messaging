package ru.inovus.messaging.impl.rest;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.criteria.ProviderRecipientCriteria;
import ru.inovus.messaging.api.model.ProviderRecipient;
import ru.inovus.messaging.api.rest.RecipientProviderRest;
import ru.inovus.messaging.impl.RecipientProvider;

@Controller
public class RecipientProviderRestImpl implements RecipientProviderRest {

    private final RecipientProvider client;

    public RecipientProviderRestImpl(RecipientProvider recipientProvider) {
        this.client = recipientProvider;
    }

    @Override
    public Page<ProviderRecipient> getProviderRecipient(ProviderRecipientCriteria criteria) {
        return client.getRecipients(criteria);
    }
}
