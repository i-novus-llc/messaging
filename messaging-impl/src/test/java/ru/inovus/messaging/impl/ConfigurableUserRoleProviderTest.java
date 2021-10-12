package ru.inovus.messaging.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import ru.inovus.messaging.api.criteria.RoleCriteria;
import ru.inovus.messaging.api.criteria.UserCriteria;
import ru.inovus.messaging.api.model.Role;
import ru.inovus.messaging.api.model.User;
import ru.inovus.messaging.impl.provider.ConfigurableUserRoleProvider;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringJUnit4ClassRunner.class)
public class ConfigurableUserRoleProviderTest {

    private ConfigurableUserRoleProvider userRoleProvider;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void before() throws IOException, JAXBException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        userRoleProvider = new ConfigurableUserRoleProvider(resourceLoader, "/userRoleProviderFieldMapping.xml", "http://user:9999", "http://role:9999");
        MockitoAnnotations.initMocks(this);
    }

    //        todo нужен тест построения query-param

    @Test
    public void testGetUsers() {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("ment", Map.of("content", List.of(userResponse(), userWithoutRoleResponse())));
        mockResponse.put("totalMent", 666);

        Mockito.doReturn(new ResponseEntity(mockResponse, HttpStatus.OK))
                .when(restTemplate).exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
        userRoleProvider.setRestTemplate(restTemplate);
        List<User> content = userRoleProvider.getUsers(new UserCriteria()).getContent();
        assertEquals(2, content.size());
        assertEquals("Username1", content.get(0).getUsername());
        assertEquals("Fio1", content.get(0).getFio());
        assertEquals("Email1", content.get(0).getEmail());
        assertEquals("Surname1", content.get(0).getSurname());
        assertEquals("Name1", content.get(0).getName());
        assertEquals("Patronymic1", content.get(0).getPatronymic());
        assertEquals(1, content.get(0).getRoles().size());

        assertEquals(0, content.get(1).getRoles().size());
    }

    @Test
    public void testGetRoles() {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("content", List.of(roleResponse()));
        mockResponse.put("totalElements", 666);

        Mockito.doReturn(new ResponseEntity(mockResponse, HttpStatus.OK))
                .when(restTemplate).exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
        userRoleProvider.setRestTemplate(restTemplate);
        List<Role> content = userRoleProvider.getRoles(new RoleCriteria()).getContent();

        assertEquals(1, content.size());
        assertEquals("1", content.get(0).getId());
        assertEquals("Name1", content.get(0).getName());
        assertEquals("Code1", content.get(0).getCode());
        assertEquals("Description1", content.get(0).getDescription());
    }

    private Map<String, Object> userResponse() {
        Map<String, Object> user = userWithoutRoleResponse();
        user.put("rolessss", List.of(roleResponse()));
        return user;
    }

    private Map<String, Object> userWithoutRoleResponse() {
        Map<String, Object> user = new HashMap<>();
        user.put("Cthulhu", "Username1");
        user.put("666", "Fio1");
        user.put("Petroleum", "Email1");
        user.put("surname14124", "Surname1");
        user.put("nameGame", "Name1");
        user.put("отчество", "Patronymic1");
        return user;
    }

    private Map<String, Object> roleResponse() {
        Map<String, Object> role = new HashMap<>();
        role.put("id1", 1);
        role.put("name1", "Name1");
        role.put("code1", "Code1");
        role.put("description1", "Description1");
        return role;
    }
}
