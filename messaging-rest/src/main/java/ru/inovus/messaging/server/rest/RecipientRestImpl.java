package ru.inovus.messaging.server.rest;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.rest.RecipientRest;
import ru.inovus.messaging.impl.RecipientService;

@Controller
public class RecipientRestImpl implements RecipientRest {

    private final RecipientService recipientService;

    public RecipientRestImpl(RecipientService recipientService) {
        this.recipientService = recipientService;
    }

    @Override
    public Page<Recipient> getSettings(RecipientCriteria criteria) {
        return recipientService.getRecipients(criteria);
    }
}
