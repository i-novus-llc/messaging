package ru.inovus.messaging.server.test;


import net.n2oapp.platform.test.autoconfigure.DefinePort;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;
import ru.inovus.messaging.api.criteria.UserSettingCriteria;
import ru.inovus.messaging.api.model.AlertType;
import ru.inovus.messaging.api.model.InfoType;
import ru.inovus.messaging.api.model.UserSetting;
import ru.inovus.messaging.api.queue.DestinationResolver;
import ru.inovus.messaging.api.queue.MqProvider;
import ru.inovus.messaging.api.rest.UserSettingRest;
import ru.inovus.messaging.server.BackendApplication;

import java.util.Collections;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {BackendApplication.class},
        properties = {"cxf.jaxrs.client.classes-scan=true",
                "cxf.jaxrs.client.classes-scan-packages=ru.i_novus.messaging.api",
                "spring.liquibase.change-log: classpath:/messaging-db/test-base-changelog.xml",
                "spring.liquibase.contexts=test"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DefinePort
@EnableEmbeddedPg
public class UserSettingServiceTest {

    private static final String USER_NAME = "admin";

    private static final Integer USER_SETTINGS_ID_1 = 1;
    private static final Integer USER_SETTINGS_ID_3 = 3;
    private static final Integer USER_SETTINGS_COMPONENT_ID = 1;
    private static final String USER_SETTINGS_NAME = "Уведомление УЛ банка о создании нового договора";
    private static final String USER_SETTING_TEMPLATE_CODE = "LKB-PAYMENT-NTF-1";
    private static final boolean USER_SETTING_IS_DISABLED = false;
    private static final String USER_SETTING_DEFAULT_ALERT_TYPE = "HIDDEN";
    private static final String USER_SETTING_IS_SEND_NOTICE = "NOTICE";
    private static final String USER_SETTING_IS_SEND_EMAIL = "EMAIL";

    private static final String USER_SETTING_ALERT_TYPE = "BLOCKER";

    private UserSettingRest userSettingRest;

    @MockBean
    public MqProvider mqProvider;

    @MockBean
    public DestinationResolver destinationResolver;


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

        assertTrue(userSetting.getDefaultInfoType().stream().anyMatch(infoType -> infoType.name().equals(USER_SETTING_IS_SEND_NOTICE)));
        assertTrue(userSetting.getDefaultInfoType().stream().anyMatch(infoType -> infoType.name().equals(USER_SETTING_IS_SEND_EMAIL)));
        assertTrue(userSetting.getInfoTypes().stream().anyMatch(infoType -> infoType.name().equals(USER_SETTING_IS_SEND_NOTICE)));
        assertTrue(userSetting.getInfoTypes().stream().anyMatch(infoType -> infoType.name().equals(USER_SETTING_IS_SEND_EMAIL)));
    }

    @Test
    public void updateUserSettingTest() {
        UserSetting setting = new UserSetting();
        setting.setDisabled(true);
        setting.setAlertType(AlertType.BLOCKER);
        setting.setInfoTypes(Collections.singletonList(InfoType.EMAIL));

        //Создаем уже непосредственно пользовательскую настроку, для пользователя 'admin' и для шаблона уведомления с иде-ром 1,
        userSettingRest.updateSetting(USER_NAME, USER_SETTINGS_ID_3, setting);

        //Получаем пользовательскую для пользователя 'admin' и для шаблона уведомления с иде-ром 1, с внесенными изменениями
        UserSetting userSetting = userSettingRest.getSetting(USER_NAME, USER_SETTINGS_ID_3);
        assertNotNull(userSetting);
        assertEquals(true, userSetting.getDisabled());

        //Дефолтное значение для вида сообщения осталось прежним, то которое задал Админ
        assertEquals(USER_SETTING_DEFAULT_ALERT_TYPE, userSetting.getDefaultAlertType().name());
        //Изменилось значение для вида сообщения, которое изменяется пользователем
        assertEquals(USER_SETTING_ALERT_TYPE, userSetting.getAlertType().name());

        //Дефолтные настройки по способам оповещения остались прежними
        assertTrue(userSetting.getDefaultInfoType().stream().anyMatch(infoType -> infoType.name().equals(USER_SETTING_IS_SEND_NOTICE)));
        assertTrue(userSetting.getDefaultInfoType().stream().anyMatch(infoType -> infoType.name().equals(USER_SETTING_IS_SEND_EMAIL)));

        //В настройках пользователя остался только способ отправки на почту
        assertFalse(userSetting.getInfoTypes().stream().anyMatch(infoType -> infoType.name().equals(USER_SETTING_IS_SEND_NOTICE)));
        assertTrue(userSetting.getInfoTypes().stream().anyMatch(infoType -> infoType.name().equals(USER_SETTING_IS_SEND_EMAIL)));
    }
}
