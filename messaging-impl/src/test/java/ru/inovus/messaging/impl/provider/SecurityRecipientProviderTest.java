package ru.inovus.messaging.impl.provider;

import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.model.Region;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.rest.api.OrganizationRestService;
import net.n2oapp.security.admin.rest.api.RegionRestService;
import net.n2oapp.security.admin.rest.api.RoleRestService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestOrganizationCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestRegionCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestRoleCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.inovus.messaging.api.criteria.ProviderRecipientCriteria;
import ru.inovus.messaging.api.criteria.SecurityBaseCriteria;
import ru.inovus.messaging.api.model.BaseResponse;
import ru.inovus.messaging.api.model.ProviderRecipient;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class SecurityRecipientProviderTest {
    private SecurityAdminRecipientProvider recipientProvider;

    private UserRestService userRestService;
    private RoleRestService roleRestService;
    private RegionRestService regionRestService;
    private OrganizationRestService organizationRestService;

    private Role role1;
    private Role role2;
    private Region region;
    private Organization organization;

    @BeforeEach
    public void before() {
        userRestService = Mockito.mock(UserRestService.class);
        roleRestService = Mockito.mock(RoleRestService.class);
        regionRestService = Mockito.mock(RegionRestService.class);
        organizationRestService = Mockito.mock(OrganizationRestService.class);

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

        role1 = new Role();
        role1.setId(1);
        role1.setName("name1");
        role1.setCode("code1");
        role1.setDescription("description1");
        user2.setRoles(List.of(role1));

        role2 = new Role();
        role2.setId(2);
        role2.setName("name2");
        role2.setCode("code2");
        role2.setDescription("description2");

        region = new Region();
        region.setId(1);
        region.setOkato("okato");
        region.setCode("code");
        region.setName("region");

        organization = new Organization();
        organization.setId(1);
        organization.setCode("code");
        organization.setShortName("organization");
        organization.setFullName("organization");

        PageImpl userPage = new PageImpl(List.of(user1, user2), new ProviderRecipientCriteria(), 2);
        Mockito.when(userRestService.findAll(new RestUserCriteria())).thenReturn(userPage);
        PageImpl rolePage = new PageImpl(List.of(role1, role2), new SecurityBaseCriteria(), 2);
        Mockito.when(roleRestService.findAll(new RestRoleCriteria())).thenReturn(rolePage);
        PageImpl regionPage = new PageImpl(List.of(region), new SecurityBaseCriteria(), 2);
        Mockito.when(regionRestService.getAll(new RestRegionCriteria())).thenReturn(regionPage);
        PageImpl organizationPage = new PageImpl(List.of(organization), new SecurityBaseCriteria(), 2);
        Mockito.when(organizationRestService.getAll(new RestOrganizationCriteria())).thenReturn(organizationPage);

        Mockito.when(roleRestService.getById(1)).thenReturn(role1);
        Mockito.when(roleRestService.getById(2)).thenReturn(role2);
        Mockito.when(organizationRestService.get(1)).thenReturn(organization);

        recipientProvider = new SecurityAdminRecipientProvider(userRestService, roleRestService, regionRestService, organizationRestService);
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

    @Test
    public void testGetUsersByMultipleOrganizations() {
        User orgUser1 = new User();
        orgUser1.setUsername("org1user");
        orgUser1.setFio("fio_org1");

        User orgUser2 = new User();
        orgUser2.setUsername("org2user");
        orgUser2.setFio("fio_org2");

        PageImpl combinedPage = new PageImpl(List.of(orgUser1, orgUser2), new ProviderRecipientCriteria(), 2);
        ArgumentCaptor<RestUserCriteria> criteriaCaptor = ArgumentCaptor.forClass(RestUserCriteria.class);
        Mockito.when(userRestService.findAll(criteriaCaptor.capture())).thenReturn(combinedPage);

        ProviderRecipientCriteria criteria = new ProviderRecipientCriteria();
        criteria.setOrganizationIds(List.of(1, 2));

        List<ProviderRecipient> content = recipientProvider.getRecipients(criteria).getContent();

        Mockito.verify(userRestService, Mockito.times(1)).findAll(any());
        assertEquals(List.of(1, 2), criteriaCaptor.getValue().getOrganizationIds());
        assertEquals(2, content.size());
        assertEquals("org1user", content.get(0).getUsername());
        assertEquals("org2user", content.get(1).getUsername());
    }

    @Test
    public void testGetRoles() {
        List<Role> content = recipientProvider.getRoles(new SecurityBaseCriteria()).getContent();
        assertEquals(2, content.size());
        assertEquals("name1", content.get(0).getName());
        assertEquals("code1", content.get(0).getCode());
        assertEquals("name2", content.get(1).getName());
        assertEquals("code2", content.get(1).getCode());
    }

    @Test
    public void testGetRegions() {
        List<Region> content = recipientProvider.getRegions(new SecurityBaseCriteria()).getContent();
        assertEquals(1, content.size());
        assertEquals("okato", content.get(0).getOkato());
        assertEquals("code", content.get(0).getCode());
        assertEquals("region", content.get(0).getName());
    }

    @Test
    public void testGetMedOrganizations() {
        List<Organization> content = recipientProvider.getMedOrganizations(new SecurityBaseCriteria()).getContent();
        assertEquals(1, content.size());
        assertEquals("code", content.get(0).getCode());
        assertEquals("organization", content.get(0).getFullName());
        assertEquals("organization", content.get(0).getShortName());
    }

    @Test
    public void testGetRecipientsBySingleOrganization() {
        User orgUser = new User();
        orgUser.setUsername("orgUser");
        orgUser.setFio("fio");
        orgUser.setEmail("email");
        orgUser.setSurname("surname");
        orgUser.setName("name");
        orgUser.setPatronymic("patronymic");

        Mockito.when(userRestService.findAll(any()))
                .thenReturn(new PageImpl(List.of(orgUser), new ProviderRecipientCriteria(), 1));

        ProviderRecipientCriteria criteria = new ProviderRecipientCriteria();
        criteria.setOrganizationIds(List.of(5));

        List<ProviderRecipient> content = recipientProvider.getRecipients(criteria).getContent();

        assertEquals(1, content.size());
        assertEquals("orgUser", content.get(0).getUsername());
    }

    @Test
    public void testGetRecipientsEmptyWhenNoUsersFound() {
        Mockito.when(userRestService.findAll(any()))
                .thenReturn(new PageImpl(List.of(), new ProviderRecipientCriteria(), 0));

        List<ProviderRecipient> content = recipientProvider.getRecipients(new ProviderRecipientCriteria()).getContent();

        assertTrue(content.isEmpty());
    }

    @Test
    public void testGetRecipientsByMultipleOrgsNoResults() {
        PageImpl emptyPage = new PageImpl(List.of(), new ProviderRecipientCriteria(), 0);
        Mockito.when(userRestService.findAll(any())).thenReturn(emptyPage);

        ProviderRecipientCriteria criteria = new ProviderRecipientCriteria();
        criteria.setOrganizationIds(List.of(1, 2));

        assertTrue(recipientProvider.getRecipients(criteria).getContent().isEmpty());
        Mockito.verify(userRestService, Mockito.times(1)).findAll(any());
    }

    @Test
    public void testGetRolesByJoinedString() {
        List<BaseResponse> roles = recipientProvider.getRoles("1, 2");

        assertEquals(2, roles.size());
        assertEquals(Integer.valueOf(1), roles.get(0).getId());
        assertEquals("name1", roles.get(0).getName());
        assertEquals(Integer.valueOf(2), roles.get(1).getId());
        assertEquals("name2", roles.get(1).getName());
    }

    @Test
    public void testGetRegionByName() {
        Mockito.when(regionRestService.getAll(any()))
                .thenReturn(new PageImpl(List.of(region), new SecurityBaseCriteria(), 1));

        BaseResponse result = recipientProvider.getRegion("region");

        assertEquals(Integer.valueOf(1), result.getId());
        assertEquals("region", result.getName());
    }

    @Test
    public void testGetRegionByNameNotFound() {
        Mockito.when(regionRestService.getAll(any()))
                .thenReturn(new PageImpl(List.of(), new SecurityBaseCriteria(), 0));

        BaseResponse result = recipientProvider.getRegion("unknown");

        assertNull(result);
    }

    @Test
    public void testGetMedOrganizationsByJoinedIds() {
        List<BaseResponse> orgs = recipientProvider.getMedOrganizations("1");

        assertEquals(1, orgs.size());
        assertEquals(Integer.valueOf(1), orgs.get(0).getId());
        assertEquals("organization", orgs.get(0).getName());
    }
}
