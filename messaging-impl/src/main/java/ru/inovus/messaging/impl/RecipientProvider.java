package ru.inovus.messaging.impl;

import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.criteria.ProviderRecipientCriteria;
import ru.inovus.messaging.api.model.ProviderRecipient;

/**
 * Провайдер для работы с получателями уведомлений
 */
public interface RecipientProvider {
    Page<ProviderRecipient> getRecipients(ProviderRecipientCriteria criteria);
}
