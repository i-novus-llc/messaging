package ru.inovus.messaging.impl;

import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.criteria.RoleCriteria;
import ru.inovus.messaging.api.criteria.UserCriteria;
import ru.inovus.messaging.api.model.Role;
import ru.inovus.messaging.api.model.User;

public interface UserRoleDataProvider {
    Page<Role> getRoles(RoleCriteria criteria);

    Page<User> getUsers(UserCriteria criteria);
}
