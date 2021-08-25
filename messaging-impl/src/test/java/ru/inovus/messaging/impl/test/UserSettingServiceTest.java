package ru.inovus.messaging.impl.test;

import net.n2oapp.platform.test.autoconfigure.DefinePort;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.inovus.messaging.api.criteria.UserSettingCriteria;
import ru.inovus.messaging.api.model.AlertType;
import ru.inovus.messaging.api.model.Channel;
import ru.inovus.messaging.api.model.UserSetting;
import ru.inovus.messaging.api.rest.UserSettingRest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {TestApp.class},
        properties = {"cxf.jaxrs.client.classes-scan=true",
                "cxf.jaxrs.client.classes-scan-packages=ru.i_novus.messaging.api",
                "spring.liquibase.change-log: classpath:/messaging-db/test-base-changelog.xml",
                "spring.liquibase.contexts=test"
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DefinePort
@EnableEmbeddedPg
@TestPropertySource("classpath:rest-test.properties")
public class UserSettingServiceTest {

    private static final String USER_NAME = "admin";

    private static final Integer USER_SETTINGS_ID_1 = 1;
    private static final Integer USER_SETTINGS_ID_3 = 3;
    private static final Integer USER_SETTINGS_COMPONENT_ID = 1;
    private static final String USER_SETTINGS_NAME = "Уведомление УЛ банка о создании нового договора";
    private static final String USER_SETTING_TEMPLATE_CODE = "LKB-PAYMENT-NTF-1";
    private static final boolean USER_SETTING_IS_DISABLED = false;
    private static final String USER_SETTING_DEFAULT_ALERT_TYPE = "HIDDEN";
    private static final String USER_SETTING_IS_SEND_NOTICE = "notice";
    private static final String USER_SETTING_IS_SEND_EMAIL = "email";

    private static final String USER_SETTING_ALERT_TYPE = "BLOCKER";

    private UserSettingRest userSettingRest;

    @Autowired
    public void setUserSettingRest(UserSettingRest userSettingRest) {
        this.userSettingRest = userSettingRest;
    }

    /**
     * Первый вариант получения настроек уведомлений для пользователя, когда есть только общие настройки,
     * которые создаются Админом и хранятся в таблице public.message_setting. В этом случае ему доступны все настройки, у которых признак is_disabled = false
     */
    @Test
    public void getUserSettingsTest() {
        UserSettingCriteria cr = new UserSettingCriteria();
        cr.setPageSize(100);
        cr.setUser(USER_NAME);
        Page<UserSetting> userSettings = userSettingRest.getSettings(cr);

        assertNotNull(userSettings);
        userSettings.stream().map(UserSetting::getDisabled).forEach(Assert::assertFalse);
    }

    /**
     * Получение настройки пользователя по тому шаблону уведомления, по которому еще нет пользовательских изменений, и все данные берутся из таблицы public.message_setting.
     */
    @Test
    public void getUserSettingTest() {
        UserSetting userSetting = userSettingRest.getSetting(USER_NAME, USER_SETTINGS_ID_1);

        assertNotNull(userSetting);
        assertEquals(USER_SETTINGS_COMPONENT_ID, userSetting.getComponent().getId());
        assertEquals(USER_SETTINGS_NAME, userSetting.getName());
        assertEquals(USER_SETTING_TEMPLATE_CODE, userSetting.getTemplateCode());
        assertEquals(USER_SETTING_IS_DISABLED, userSetting.getDisabled());

        assertEquals(USER_SETTING_DEFAULT_ALERT_TYPE, userSetting.getDefaultAlertType().name());
        assertEquals(USER_SETTING_DEFAULT_ALERT_TYPE, userSetting.getAlertType().name());

        assertEquals(USER_SETTING_IS_SEND_EMAIL, userSetting.getChannel().getId());
    }

    @Test
    public void updateUserSettingTest() {
        UserSetting setting = new UserSetting();
        setting.setDisabled(true);
        setting.setAlertType(AlertType.BLOCKER);
        setting.setChannel(new Channel(USER_SETTING_IS_SEND_NOTICE, "Центр уведомлений", USER_SETTING_IS_SEND_NOTICE));

        //Создаем уже непосредственно пользовательскую настройку, для пользователя 'admin' и для шаблона уведомления с иде-ром 1,
        userSettingRest.updateSetting(USER_NAME, USER_SETTINGS_ID_3, setting);

        //Получаем пользовательскую для пользователя 'admin' и для шаблона уведомления с иде-ром 1, с внесенными изменениями
        UserSetting userSetting = userSettingRest.getSetting(USER_NAME, USER_SETTINGS_ID_3);
        assertNotNull(userSetting);
        assertEquals(true, userSetting.getDisabled());

        //Дефолтное значение для вида сообщения осталось прежним, то которое задал Админ
        assertEquals(USER_SETTING_DEFAULT_ALERT_TYPE, userSetting.getDefaultAlertType().name());
        //Изменилось значение для вида сообщения, которое изменяется пользователем
        assertEquals(USER_SETTING_ALERT_TYPE, userSetting.getAlertType().name());

        //В настройках пользователя способ отправки на почту
        assertEquals(USER_SETTING_IS_SEND_NOTICE, userSetting.getChannel().getId());
    }
}
