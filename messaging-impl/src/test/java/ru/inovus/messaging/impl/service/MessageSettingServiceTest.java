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
import ru.inovus.messaging.api.model.MessageSetting;
import ru.inovus.messaging.api.model.enums.AlertType;
import ru.inovus.messaging.api.model.enums.FormationType;
import ru.inovus.messaging.api.model.enums.Severity;
import ru.inovus.messaging.api.model.enums.YesNo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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
    public void test() {
        Integer id = testCreateSetting();
        testUpdateSetting(id);
        testDeleteSetting(id);
    }

    private Integer testCreateSetting() {

    }

    private Integer testUpdateSetting(Integer id) {

    }

    private void testDeleteSetting(Integer id) {

    }
}
