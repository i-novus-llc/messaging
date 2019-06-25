package ru.inovus.messaging.server.rest;

import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.rest.api.UserRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.criteria.UserCriteria;
import ru.inovus.messaging.api.rest.UserRest;

@Controller
public class UserRestImpl implements UserRest {

    private final UserRestService client;

    public UserRestImpl(@Qualifier("userRestServiceJaxRsProxyClient") UserRestService client) {
        this.client = client;
    }

    @Override
    public Page<User> getUsers(UserCriteria criteria) {
        RestUserCriteria restUserCriteria = new RestUserCriteria();
        restUserCriteria.setUsername(criteria.getUsername());
        restUserCriteria.setPage(criteria.getPageNumber());
        restUserCriteria.setSize(criteria.getPageSize());
        return client.findAll(restUserCriteria);
    }
}
