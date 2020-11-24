package ru.inovus.messaging.server.rest;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.criteria.UserCriteria;
import ru.inovus.messaging.api.model.User;
import ru.inovus.messaging.api.rest.UserRest;
import ru.inovus.messaging.impl.UserRoleDataProvider;

@Controller
public class UserRestImpl implements UserRest {

    private final UserRoleDataProvider client;

    public UserRestImpl(UserRoleDataProvider userRoleDataProvider) {
        this.client = userRoleDataProvider;
    }

    @Override
    public Page<User> getUsers(UserCriteria criteria) {
        return client.getUsers(criteria);
    }
}
