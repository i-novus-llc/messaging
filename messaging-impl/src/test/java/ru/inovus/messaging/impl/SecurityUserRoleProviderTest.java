package ru.inovus.messaging.impl;

import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.rest.api.RoleRestService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestRoleCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.inovus.messaging.api.criteria.RoleCriteria;
import ru.inovus.messaging.api.criteria.UserCriteria;
import ru.inovus.messaging.impl.provider.SecurityAdminUserRoleProvider;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
public class SecurityUserRoleProviderTest {
    private SecurityAdminUserRoleProvider userRoleProvider;

    @Mock
    private UserRestService userRestService;

    @Mock
    private RoleRestService roleRestService;

    @Before
    public void before() {
        User user1 = new User();
        user1.setUsername("username1");
        user1.setFio("fio1");
        user1.setEmail("email1");
        user1.setSurname("surname1");
        user1.setName("name1");
        user1.setPatronymic("patronymic1");

        User user2 = new User();
        user2.setUsername("username2");
        user2.setFio("fio2");
        user2.setEmail("email2");
        user2.setSurname("surname2");
        user2.setName("name2");
        user2.setPatronymic("patronymic2");
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("name1");
        role1.setCode("code1");
        role1.setDescription("description1");
        user2.setRoles(List.of(role1));

        PageImpl userPage = new PageImpl(List.of(user1, user2), new UserCriteria(), 2);
        Mockito.when(userRestService.findAll(new RestUserCriteria())).thenReturn(userPage);

        PageImpl rolePage = new PageImpl(List.of(role1), new RoleCriteria(), 1);
        Mockito.when(roleRestService.findAll(new RestRoleCriteria())).thenReturn(rolePage);

        userRoleProvider = new SecurityAdminUserRoleProvider(userRestService, roleRestService);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUsers() {
        List<ru.inovus.messaging.api.model.User> content = userRoleProvider.getUsers(new UserCriteria()).getContent();
        assertEquals(2, content.size());
        assertEquals("username1", content.get(0).getUsername());
        assertEquals("fio1", content.get(0).getFio());
        assertEquals("email1", content.get(0).getEmail());
        assertEquals("surname1", content.get(0).getSurname());
        assertEquals("name1", content.get(0).getName());
        assertEquals("patronymic1", content.get(0).getPatronymic());
        assertEquals(0, content.get(0).getRoles().size());

        assertEquals("username2", content.get(1).getUsername());
        assertEquals("fio2", content.get(1).getFio());
        assertEquals("email2", content.get(1).getEmail());
        assertEquals("surname2", content.get(1).getSurname());
        assertEquals("name2", content.get(1).getName());
        assertEquals("patronymic2", content.get(1).getPatronymic());
        assertEquals(1, content.get(1).getRoles().size());
    }

    @Test
    public void testGetRoles() {
        List<ru.inovus.messaging.api.model.Role> content = userRoleProvider.getRoles(new RoleCriteria()).getContent();
        assertEquals(1, content.size());
        assertEquals("1", content.get(0).getId());
        assertEquals("name1", content.get(0).getName());
        assertEquals("code1", content.get(0).getCode());
        assertEquals("description1", content.get(0).getDescription());
    }
}
