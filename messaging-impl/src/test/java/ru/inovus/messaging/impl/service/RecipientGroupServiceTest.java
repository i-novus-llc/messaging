package ru.inovus.messaging.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.platform.test.autoconfigure.pg.EnableTestcontainersPg;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.inovus.messaging.TestApp;
import ru.inovus.messaging.api.criteria.RecipientGroupCriteria;
import ru.inovus.messaging.api.model.MessageTemplate;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.RecipientGroup;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableTestcontainersPg
public class RecipientGroupServiceTest {

    @Autowired
    RecipientGroupService service;
    @Autowired
    MessageTemplateService messageTemplateService;

    private static final String TENANT_CODE = "tenant";

    @Test
    public void crudMinimal() {
        RecipientGroup group = getNewGroup("Group1", "Code1", "Desc1");

        Integer id = service.createRecipientGroup(TENANT_CODE, group);
        assertThat(id, notNullValue());

        RecipientGroup bdGroup = service.getRecipientGroup(TENANT_CODE, id, null);
        assertThat(bdGroup, notNullValue());
        assertThat(bdGroup.getId(), is(id));
        assertThat(bdGroup.getName(), is("Group1"));
        assertThat(bdGroup.getDescription(), is("Desc1"));
        assertThat(bdGroup.getRecipients().size(), is(0));
        assertThat(bdGroup.getTemplates().size(), is(0));

        group.setName("Group11");
        group.setDescription(null);
        service.updateRecipientGroup(TENANT_CODE, id, group);

        bdGroup = service.getRecipientGroup(TENANT_CODE, null, "Code1");
        assertThat(bdGroup, notNullValue());
        assertThat(bdGroup.getId(), is(id));
        assertThat(bdGroup.getName(), is("Group11"));
        assertThat(bdGroup.getDescription(), nullValue());
        assertThat(bdGroup.getRecipients().size(), is(0));
        assertThat(bdGroup.getTemplates().size(), is(0));

        service.deleteRecipientGroup(TENANT_CODE, id);
        bdGroup = service.getRecipientGroup(TENANT_CODE, id, "Code1");
        assertThat(bdGroup, nullValue());
    }

    @Test
    public void crudFull() {
        RecipientGroup group = getNewGroup("Group2", "Code2", null);

        addUser(group, "Мускатова Ольга (443-095-664 87)", "443-095-664 87");
        addUser(group, "Андреев Юлиан (830 792 718 20)", "830 792 718 20");

        addTemplate(group, 1, "mt1");
        addTemplate(group, 2, "mt2");

        Integer id = service.createRecipientGroup(TENANT_CODE, group);
        assertThat(id, notNullValue());

        RecipientGroup bdGroup = service.getRecipientGroup(TENANT_CODE, id, null);
        assertThat(bdGroup, notNullValue());
        assertThat(bdGroup.getId(), is(id));
        assertThat(bdGroup.getName(), is("Group2"));
        assertThat(bdGroup.getCode(), is("Code2"));
        assertThat(bdGroup.getDescription(), nullValue());

        List<Recipient> bbUsers = bdGroup.getRecipients();
        List<MessageTemplate> bbTemplates = bdGroup.getTemplates();

        assertThat(bbUsers.size(), is(2));
        assertThat(bbTemplates.size(), is(2));

        MessageTemplate dbMT1 = bbTemplates.get(0).getId() == 1 ? bbTemplates.get(0) : bbTemplates.get(1);
        assertThat(dbMT1.getId(), is(1));
        assertThat(dbMT1.getCode(), is("mt1"));

        MessageTemplate dbMT2 = bbTemplates.get(0).getId() == 2 ? bbTemplates.get(0) : bbTemplates.get(1);
        assertThat(dbMT2.getId(), is(2));
        assertThat(dbMT2.getCode(), is("mt2"));

        Recipient dbRc1 = bbUsers.get(0).getUsername().equals("443-095-664 87") ? bbUsers.get(0) : bbUsers.get(1);
        Recipient dbRc2 = bbUsers.get(0).getUsername().equals("830 792 718 20") ? bbUsers.get(0) : bbUsers.get(1);

        assertThat(dbRc1.getUsername(), is("443-095-664 87"));
        assertThat(dbRc1.getName(), is("Мускатова Ольга (443-095-664 87)"));

        assertThat(dbRc2.getUsername(), is("830 792 718 20"));
        assertThat(dbRc2.getName(), is("Андреев Юлиан (830 792 718 20)"));

        group.setName("Group22");
        group.setCode("Code22");
        group.setDescription("Desc22");
        group.getRecipients().remove(1);
        addUser(group, "Фомин Антон (725-468-255 19)", "725-468-255 19");
        group.getTemplates().remove(1);
        addTemplate(group, 3, "mt3");

        service.updateRecipientGroup(TENANT_CODE, id, group);

        bdGroup = service.getRecipientGroup(TENANT_CODE, id, null);
        assertThat(bdGroup, notNullValue());
        assertThat(bdGroup.getId(), is(id));
        assertThat(bdGroup.getName(), is("Group22"));
        assertThat(bdGroup.getCode(), is("Code22"));
        assertThat(bdGroup.getDescription(), is("Desc22"));

        bbUsers = bdGroup.getRecipients();
        bbTemplates = bdGroup.getTemplates();

        assertThat(bbUsers.size(), is(2));
        assertThat(bbTemplates.size(), is(2));

        dbMT1 = bbTemplates.get(0).getId() == 1 ? bbTemplates.get(0) : bbTemplates.get(1);
        assertThat(dbMT1.getId(), is(1));
        assertThat(dbMT1.getCode(), is("mt1"));

        dbMT2 = bbTemplates.get(0).getId() == 3 ? bbTemplates.get(0) : bbTemplates.get(1);
        assertThat(dbMT2.getId(), is(3));
        assertThat(dbMT2.getCode(), is("mt3"));

        dbRc1 = bbUsers.get(0).getUsername().equals("443-095-664 87") ? bbUsers.get(0) : bbUsers.get(1);
        dbRc2 = bbUsers.get(0).getUsername().equals("830 792 718 20") ? bbUsers.get(0) : bbUsers.get(1);

        assertThat(dbRc1.getUsername(), is("443-095-664 87"));
        assertThat(dbRc1.getName(), is("Мускатова Ольга (443-095-664 87)"));

        assertThat(dbRc2.getUsername(), is("725-468-255 19"));
        assertThat(dbRc2.getName(), is("Фомин Антон (725-468-255 19)"));

        service.deleteRecipientGroup(TENANT_CODE, id);
        bdGroup = service.getRecipientGroup(TENANT_CODE, id, "Code22");
        assertThat(bdGroup, nullValue());
    }


    @Test
    public void filterCheck() {
        RecipientGroupCriteria criteria = new RecipientGroupCriteria();
        Page<RecipientGroup> list = service.getRecipientGroups(TENANT_CODE, criteria);
        Long beforeCount = list.getTotalElements();

        RecipientGroup group = getNewGroup("Group31", "Code31", null);
        addUser(group, "user1", "userName1");
        addUser(group, "user2", "userName2");
        addTemplate(group, 1, "mt1");
        addTemplate(group, 2, "mt2");
        Integer id = service.createRecipientGroup(TENANT_CODE, group);

        group = getNewGroup("Group32", "Code32", null);
        addUser(group, "user1", "userName1");
        addTemplate(group, 1, "mt1");
        service.createRecipientGroup(TENANT_CODE, group);

        group = getNewGroup("Group33", "Code33", null);
        addUser(group, "user2", "userName2");
        addTemplate(group, 2, "mt2");
        service.createRecipientGroup(TENANT_CODE, group);

        group = getNewGroup("Group34", "Code34", null);
        addUser(group, "fuser3", "userName3");
        addTemplate(group, 3, "mt3");
        service.createRecipientGroup(TENANT_CODE, group);

        criteria = new RecipientGroupCriteria();
        list = service.getRecipientGroups(TENANT_CODE, criteria);
        assertThat(list.getTotalElements(), is(beforeCount + 4L));

        criteria.setName("Group31");
        list = service.getRecipientGroups(TENANT_CODE, criteria);
        assertThat(list.getTotalElements(), is(1L));
        RecipientGroup rg = list.getContent().get(0);
        assertThat(rg.getName(), is("Group31"));

        criteria.setName(null);
        criteria.setTemplateCodes(List.of("mt2"));
        list = service.getRecipientGroups(TENANT_CODE, criteria);
        assertThat(list.getTotalElements(), is(2L));
        assertThat(getTemplateByCode(list.getContent().get(0).getTemplates(), "mt2"), notNullValue());
        assertThat(getTemplateByCode(list.getContent().get(1).getTemplates(), "mt2"), notNullValue());

        criteria.setTemplateCodes(null);
        criteria.setRecipientNames(List.of("userName1"));
        list = service.getRecipientGroups(TENANT_CODE, criteria);
        assertThat(list.getTotalElements(), is(2L));
        assertThat(getGroupByName(list.getContent(), "Group31"), notNullValue());
        assertThat(getGroupByName(list.getContent(), "Group32"), notNullValue());
        assertThat(getRecipientByName(list.getContent().get(0).getRecipients() ,"user1"), notNullValue());
        assertThat(getRecipientByName(list.getContent().get(1).getRecipients() ,"user1"), notNullValue());

        criteria.setRecipientNames(List.of("userName3"));
        list = service.getRecipientGroups(TENANT_CODE, criteria);
        assertThat(list.getTotalElements(), is(1L));
        assertThat(list.getContent().get(0).getName(), is("Group34"));
        assertThat(getRecipientByName(list.getContent().get(0).getRecipients() ,"fuser3"), notNullValue());
    }

    @Test
    public void codeValidatorTest() {
        RecipientGroup group = getNewGroup("Group41", "Code41", "Desc41");
        Integer id1 = service.createRecipientGroup(TENANT_CODE, group);
        group = getNewGroup("Group42", "Code42", "Desc42");
        Integer id2 = service.createRecipientGroup(TENANT_CODE, group);

        final RecipientGroup fGroup1 = getNewGroup("Group42", "Code42", "Desc42");
        assertThrows(UserException.class, () -> service.createRecipientGroup(TENANT_CODE, fGroup1), "messaging.exception.recipientGroup.codeExist");

        final RecipientGroup fGroup2 = getNewGroup("Group42", "Code41", "Desc42");
        assertThrows(UserException.class, () -> service.updateRecipientGroup(TENANT_CODE, id2, fGroup2), "messaging.exception.recipientGroup.codeExist");

        service.deleteRecipientGroup(TENANT_CODE, id1);
        service.deleteRecipientGroup(TENANT_CODE, id2);
    }

    private RecipientGroup getGroupByName(List<RecipientGroup> content, String name) {
        return content.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    private RecipientGroup getNewGroup(String name, String code, String desc) {
        RecipientGroup result = new RecipientGroup();
        result.setName(name);
        result.setCode(code);
        result.setDescription(desc);
        result.setRecipients(new ArrayList<>());
        result.setTemplates(new ArrayList<>());
        return result;
    }

    private Recipient addUser(RecipientGroup grp, String name, String userName) {
        Recipient recipient = new Recipient(userName);
        recipient.setName(name);
        grp.getRecipients().add(recipient);
        return recipient;
    }

    private MessageTemplate addTemplate(RecipientGroup grp, Integer id, String code) {
        MessageTemplate template = new MessageTemplate(id);
        template.setCode(code);
        grp.getTemplates().add(template);
        return template;
    }

    private MessageTemplate getTemplateByCode(List<MessageTemplate> templates, String code) {
        return templates.stream().filter(t -> t.getCode().equals(code)).findFirst().orElse(null);
    }

    private Recipient getRecipientByName(List<Recipient> recipients, String name) {
        return recipients.stream().filter(r -> r.getName().toLowerCase().startsWith(name.toLowerCase()))
                .findFirst().orElse(null);
    }
}
