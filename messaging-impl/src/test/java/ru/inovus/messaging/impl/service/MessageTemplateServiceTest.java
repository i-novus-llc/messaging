package ru.inovus.messaging.impl.service;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.inovus.messaging.TestApp;
import ru.inovus.messaging.api.criteria.MessageTemplateCriteria;
import ru.inovus.messaging.api.model.Channel;
import ru.inovus.messaging.api.model.MessageTemplate;
import ru.inovus.messaging.api.model.enums.AlertType;
import ru.inovus.messaging.api.model.enums.FormationType;
import ru.inovus.messaging.api.model.enums.Severity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.liquibase.change-log: classpath:/messaging-db/test-base-changelog.xml")
@EnableEmbeddedPg
public class MessageTemplateServiceTest {

    @Autowired
    private MessageTemplateService service;

    private static final String TENANT_CODE = "tenant";


    @Test
    public void testGetTemplates() {
        MessageTemplateCriteria criteria = new MessageTemplateCriteria();

        Page<MessageTemplate> templates = service.getTemplates(TENANT_CODE, criteria);
        // order by id desc
        assertThat(templates.getTotalElements(), is(3L));
        assertThat(templates.getContent().get(0).getId(), is(3));
        assertThat(templates.getContent().get(1).getId(), is(2));
        assertThat(templates.getContent().get(2).getId(), is(1));

        // filter by code
        criteria.setCode("mt2");
        templates = service.getTemplates(TENANT_CODE, criteria);
        assertThat(templates.getTotalElements(), is(1L));
        assertThat(templates.getContent().get(0).getId(), is(2));

        // filter by name
        criteria = new MessageTemplateCriteria();
        criteria.setName("msg_template3");
        templates = service.getTemplates(TENANT_CODE, criteria);
        assertThat(templates.getTotalElements(), is(1L));
        assertThat(templates.getContent().get(0).getId(), is(3));

        // filter by alert type
        criteria = new MessageTemplateCriteria();
        criteria.setAlertType(AlertType.HIDDEN);
        templates = service.getTemplates(TENANT_CODE, criteria);
        assertThat(templates.getTotalElements(), is(2L));
        assertThat(templates.getContent().get(0).getId(), is(3));
        assertThat(templates.getContent().get(1).getId(), is(2));

        // filter by severity
        criteria = new MessageTemplateCriteria();
        criteria.setSeverity(Severity.INFO);
        templates = service.getTemplates(TENANT_CODE, criteria);
        assertThat(templates.getTotalElements(), is(2L));
        assertThat(templates.getContent().get(0).getId(), is(2));
        assertThat(templates.getContent().get(1).getId(), is(1));

        // filter by formation type
        criteria = new MessageTemplateCriteria();
        criteria.setFormationType(FormationType.AUTO);
        templates = service.getTemplates(TENANT_CODE, criteria);
        assertThat(templates.getTotalElements(), is(2L));
        assertThat(templates.getContent().get(0).getId(), is(3));
        assertThat(templates.getContent().get(1).getId(), is(2));

        // filter by enabled
        criteria = new MessageTemplateCriteria();
        criteria.setEnabled(Boolean.TRUE);
        templates = service.getTemplates(TENANT_CODE, criteria);
        assertThat(templates.getTotalElements(), is(2L));
        assertThat(templates.getContent().get(0).getId(), is(3));
        assertThat(templates.getContent().get(1).getId(), is(1));

        // filter by channel code
        criteria = new MessageTemplateCriteria();
        criteria.setChannelCode("email");
        templates = service.getTemplates(TENANT_CODE, criteria);
        assertThat(templates.getTotalElements(), is(2L));
        assertThat(templates.getContent().get(0).getId(), is(3));
        assertThat(templates.getContent().get(1).getId(), is(2));
    }

    @Test
    public void testGetByCode() {
        // return null if not exist
        MessageTemplate template = service.getTemplate("undefined");
        assertThat(template, nullValue());

        template = service.getTemplate("mt2");
        assertThat(template.getId(), is(2));
        assertThat(template.getCode(), is("mt2"));
    }

    @Test
    public void test() {
        Integer id = testCreateTemplate();
        testUpdateTemplate(id);
        testDeleteTemplate(id);
    }

    private Integer testCreateTemplate() {
        MessageTemplate newTemplate = new MessageTemplate();
        newTemplate.setName("Test template");
        newTemplate.setCode("test");
        newTemplate.setCaption("caption");
        newTemplate.setText("text");
        newTemplate.setSeverity(Severity.ERROR);
        newTemplate.setFormationType(FormationType.AUTO);
        newTemplate.setAlertType(AlertType.BLOCKER);
        newTemplate.setChannel(new Channel("web", "Web", "web_queue"));
        newTemplate.setEnabled(true);
        Integer newTemplateId = service.createTemplate(TENANT_CODE, newTemplate).getId();

        MessageTemplate template = service.getTemplate(newTemplateId);
        assertThat(template.getName(), is(newTemplate.getName()));
        assertThat(template.getCode(), is(newTemplate.getCode()));
        assertThat(template.getCaption(), is(newTemplate.getCaption()));
        assertThat(template.getText(), is(newTemplate.getText()));
        assertThat(template.getSeverity(), is(newTemplate.getSeverity()));
        assertThat(template.getFormationType(), is(newTemplate.getFormationType()));
        assertThat(template.getAlertType(), is(newTemplate.getAlertType()));
        assertThat(template.getChannel().getId(), is(newTemplate.getChannel().getId()));
        assertThat(template.getEnabled(), is(newTemplate.getEnabled()));

        return newTemplateId;
    }

    private void testUpdateTemplate(Integer id) {
        MessageTemplate updatedTemplate = new MessageTemplate();
        updatedTemplate.setName("Test template2");
        updatedTemplate.setCode("test2");
        updatedTemplate.setCaption("caption2");
        updatedTemplate.setText("text2");
        updatedTemplate.setSeverity(Severity.INFO);
        updatedTemplate.setFormationType(FormationType.HAND);
        updatedTemplate.setAlertType(AlertType.HIDDEN);
        updatedTemplate.setChannel(new Channel("email", "Email", "email_queue"));
        updatedTemplate.setEnabled(false);
        service.updateTemplate(id, updatedTemplate);

        MessageTemplate template = service.getTemplate(id);
        assertThat(template.getName(), is(updatedTemplate.getName()));
        assertThat(template.getCode(), is(updatedTemplate.getCode()));
        assertThat(template.getCaption(), is(updatedTemplate.getCaption()));
        assertThat(template.getText(), is(updatedTemplate.getText()));
        assertThat(template.getSeverity(), is(updatedTemplate.getSeverity()));
        assertThat(template.getFormationType(), is(updatedTemplate.getFormationType()));
        assertThat(template.getAlertType(), is(updatedTemplate.getAlertType()));
        assertThat(template.getChannel().getId(), is(updatedTemplate.getChannel().getId()));
        assertThat(template.getEnabled(), is(updatedTemplate.getEnabled()));
    }

    private void testDeleteTemplate(Integer id) {
        service.deleteTemplate(id);
        MessageTemplate template = service.getTemplate(id);
        assertThat(template, nullValue());
    }
}
