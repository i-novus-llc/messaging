package ru.inovus.messaging.impl.rest;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.criteria.RecipientGroupCriteria;
import ru.inovus.messaging.api.model.RecipientGroup;
import ru.inovus.messaging.api.rest.RecipientGroupRest;
import ru.inovus.messaging.impl.service.RecipientGroupService;

@Controller
public class RecipientGroupRestImpl implements RecipientGroupRest {

    private final RecipientGroupService recipientGroupService;

    public RecipientGroupRestImpl(RecipientGroupService recipientGroupService) {
        this.recipientGroupService = recipientGroupService;
    }

    @Override
    public Page<RecipientGroup> getRecipientGroups(String tenantCode, RecipientGroupCriteria criteria) {
        return recipientGroupService.getRecipientGroups(tenantCode, criteria);
    }

    @Override
    public RecipientGroup getRecipientGroup(String tenantCode, Integer id) {
        return recipientGroupService.getRecipientGroup(tenantCode, id, null);
    }

    @Override
    public void createRecipientGroup(String tenantCode, RecipientGroup recipientGroup) {
        recipientGroupService.createRecipientGroup(tenantCode, recipientGroup);
    }

    @Override
    public void updateRecipientGroup(String tenantCode, Integer id, RecipientGroup recipientGroup) {
        recipientGroupService.updateRecipientGroup(tenantCode, id, recipientGroup);
    }

    @Override
    public void deleteRecipientGroup(String tenantCode, Integer id) {
        recipientGroupService.deleteRecipientGroup(tenantCode, id);
    }
}
