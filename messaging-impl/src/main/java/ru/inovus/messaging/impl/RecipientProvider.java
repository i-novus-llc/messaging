package ru.inovus.messaging.impl;

import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.criteria.ProviderRecipientCriteria;
import ru.inovus.messaging.api.model.RecipientFromProvider;

public interface RecipientProvider {
    Page<RecipientFromProvider> getUsers(ProviderRecipientCriteria criteria);
}
