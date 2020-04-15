package ru.inovus.messaging.api.criteria;

import net.n2oapp.platform.jaxrs.RestCriteria;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

public class BaseMessagingCriteria extends RestCriteria {
    @Override
    protected List<Sort.Order> getDefaultOrders() {
        return Collections.singletonList(new Sort.Order(Sort.Direction.DESC, "id"));
    }
}
