package ru.inovus.messaging.impl.rest;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.rest.RecipientRest;
import ru.inovus.messaging.impl.service.RecipientService;

import java.util.ArrayList;

@Controller
public class RecipientRestImpl implements RecipientRest {
    private final RecipientService recipientService;

    public RecipientRestImpl(RecipientService recipientService) {
        this.recipientService = recipientService;
    }

    @Override
    public Page<Recipient> getRecipients(RecipientCriteria criteria) {
        Page<Recipient> recipientPage = recipientService.getRecipients(criteria);
        recipientService.enrichRecipient(new ArrayList<>(recipientPage.getContent()));
        return recipientPage;
    }
}
