package ru.inovus.messaging;

import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.rest.api.UserRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.inovus.messaging.api.criteria.ProviderRecipientCriteria;
import ru.inovus.messaging.api.model.ProviderRecipient;
import ru.inovus.messaging.impl.provider.SecurityAdminRecipientProvider;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
public class SecurityRecipientProviderTest {
    private SecurityAdminRecipientProvider recipientProvider;

    @Mock
    private UserRestService userRestService;


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

        PageImpl userPage = new PageImpl(List.of(user1, user2), new ProviderRecipientCriteria(), 2);
        Mockito.when(userRestService.findAll(new RestUserCriteria())).thenReturn(userPage);

        recipientProvider = new SecurityAdminRecipientProvider(userRestService);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUsers() {
        List<ProviderRecipient> content = recipientProvider.getRecipients(new ProviderRecipientCriteria()).getContent();
        assertEquals(2, content.size());
        assertEquals("username1", content.get(0).getUsername());
        assertEquals("fio1", content.get(0).getFio());
        assertEquals("email1", content.get(0).getEmail());
        assertEquals("surname1", content.get(0).getSurname());
        assertEquals("name1", content.get(0).getName());
        assertEquals("patronymic1", content.get(0).getPatronymic());

        assertEquals("username2", content.get(1).getUsername());
        assertEquals("fio2", content.get(1).getFio());
        assertEquals("email2", content.get(1).getEmail());
        assertEquals("surname2", content.get(1).getSurname());
        assertEquals("name2", content.get(1).getName());
        assertEquals("patronymic2", content.get(1).getPatronymic());
    }
}
