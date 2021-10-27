package ru.inovus.messaging.impl.service;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.inovus.messaging.TestApp;
import ru.inovus.messaging.api.criteria.MessageSettingCriteria;
import ru.inovus.messaging.api.model.Channel;
import ru.inovus.messaging.api.model.MessageSetting;
import ru.inovus.messaging.api.model.enums.AlertType;
import ru.inovus.messaging.api.model.enums.FormationType;
import ru.inovus.messaging.api.model.enums.Severity;
import ru.inovus.messaging.api.model.enums.YesNo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.liquibase.change-log: classpath:/messaging-db/test-base-changelog.xml")
@EnableEmbeddedPg
public class MessageSettingServiceTest {

    @Autowired
    private MessageSettingService service;

    private static final String TENANT_CODE = "tenant";


    @Test
    public void testGetSettings() {
        MessageSettingCriteria criteria = new MessageSettingCriteria();

        Page<MessageSetting> settings = service.getSettings(TENANT_CODE, criteria);
        // order by id desc
        assertThat(settings.getTotalElements(), is(3L));
        assertThat(settings.getContent().get(0).getId(), is(3));
        assertThat(settings.getContent().get(1).getId(), is(2));
        assertThat(settings.getContent().get(2).getId(), is(1));

        // filter by code
        criteria.setCode("ms2");
        settings = service.getSettings(TENANT_CODE, criteria);
        assertThat(settings.getTotalElements(), is(1L));
        assertThat(settings.getContent().get(0).getId(), is(2));

        // filter by name
        criteria = new MessageSettingCriteria();
        criteria.setName("msg_setting3");
        settings = service.getSettings(TENANT_CODE, criteria);
        assertThat(settings.getTotalElements(), is(1L));
        assertThat(settings.getContent().get(0).getId(), is(3));

        // filter by alert type
        criteria = new MessageSettingCriteria();
        criteria.setAlertType(AlertType.HIDDEN);
        settings = service.getSettings(TENANT_CODE, criteria);
        assertThat(settings.getTotalElements(), is(2L));
        assertThat(settings.getContent().get(0).getId(), is(3));
        assertThat(settings.getContent().get(1).getId(), is(2));

        // filter by severity
        criteria = new MessageSettingCriteria();
        criteria.setSeverity(Severity.INFO);
        settings = service.getSettings(TENANT_CODE, criteria);
        assertThat(settings.getTotalElements(), is(2L));
        assertThat(settings.getContent().get(0).getId(), is(2));
        assertThat(settings.getContent().get(1).getId(), is(1));

        // filter by formation type
        criteria = new MessageSettingCriteria();
        criteria.setFormationType(FormationType.AUTO);
        settings = service.getSettings(TENANT_CODE, criteria);
        assertThat(settings.getTotalElements(), is(2L));
        assertThat(settings.getContent().get(0).getId(), is(3));
        assertThat(settings.getContent().get(1).getId(), is(2));

        // filter by enabled
        criteria = new MessageSettingCriteria();
        criteria.setEnabled(YesNo.YES);
        settings = service.getSettings(TENANT_CODE, criteria);
        assertThat(settings.getTotalElements(), is(2L));
        assertThat(settings.getContent().get(0).getId(), is(2));
        assertThat(settings.getContent().get(1).getId(), is(1));

        // filter by channel id
        criteria = new MessageSettingCriteria();
        criteria.setChannelId(2);
        settings = service.getSettings(TENANT_CODE, criteria);
        assertThat(settings.getTotalElements(), is(2L));
        assertThat(settings.getContent().get(0).getId(), is(3));
        assertThat(settings.getContent().get(1).getId(), is(2));
    }

    @Test
    public void testGetByCode() {
        // return null if not exist
        MessageSetting setting = service.getSetting("undefined");
        assertThat(setting, nullValue());

        setting = service.getSetting("ms2");
        assertThat(setting.getId(), is(2));
        assertThat(setting.getCode(), is("ms2"));
    }

    @Test
    public void test() {
        Integer id = testCreateSetting();
        testUpdateSetting(id);
        testDeleteSetting(id);
    }

    private Integer testCreateSetting() {
        MessageSetting newSetting = new MessageSetting();
        newSetting.setName("Test setting");
        newSetting.setCode("test");
        newSetting.setCaption("caption");
        newSetting.setText("text");
        newSetting.setSeverity(Severity.ERROR);
        newSetting.setFormationType(FormationType.AUTO);
        newSetting.setAlertType(AlertType.BLOCKER);
        newSetting.setChannel(new Channel(1, "notice", "web_queue"));
        newSetting.setDisabled(false);
        Integer newSettingId = service.createSetting(TENANT_CODE, newSetting).getId();

        MessageSetting setting = service.getSetting(newSettingId);
        assertThat(setting.getName(), is(newSetting.getName()));
        assertThat(setting.getCode(), is(newSetting.getCode()));
        assertThat(setting.getCaption(), is(newSetting.getCaption()));
        assertThat(setting.getText(), is(newSetting.getText()));
        assertThat(setting.getSeverity(), is(newSetting.getSeverity()));
        assertThat(setting.getFormationType(), is(newSetting.getFormationType()));
        assertThat(setting.getAlertType(), is(newSetting.getAlertType()));
        assertThat(setting.getChannel().getId(), is(newSetting.getChannel().getId()));
        assertThat(setting.getDisabled(), is(newSetting.getDisabled()));

        return newSettingId;
    }

    private void testUpdateSetting(Integer id) {
        MessageSetting updatedSetting = new MessageSetting();
        updatedSetting.setName("Test setting2");
        updatedSetting.setCode("test2");
        updatedSetting.setCaption("caption2");
        updatedSetting.setText("text2");
        updatedSetting.setSeverity(Severity.INFO);
        updatedSetting.setFormationType(FormationType.HAND);
        updatedSetting.setAlertType(AlertType.HIDDEN);
        updatedSetting.setChannel(new Channel(2, "email", "email_queue"));
        updatedSetting.setDisabled(true);
        service.updateSetting(id, updatedSetting);

        MessageSetting setting = service.getSetting(id);
        assertThat(setting.getName(), is(updatedSetting.getName()));
        assertThat(setting.getCode(), is(updatedSetting.getCode()));
        assertThat(setting.getCaption(), is(updatedSetting.getCaption()));
        assertThat(setting.getText(), is(updatedSetting.getText()));
        assertThat(setting.getSeverity(), is(updatedSetting.getSeverity()));
        assertThat(setting.getFormationType(), is(updatedSetting.getFormationType()));
        assertThat(setting.getAlertType(), is(updatedSetting.getAlertType()));
        assertThat(setting.getChannel().getId(), is(updatedSetting.getChannel().getId()));
        assertThat(setting.getDisabled(), is(updatedSetting.getDisabled()));
    }

    private void testDeleteSetting(Integer id) {
        service.deleteSetting(id);
        MessageSetting setting = service.getSetting(id);
        assertThat(setting, nullValue());
    }
}
