package ru.inovus.messaging.impl.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.criteria.RoleCriteria;
import ru.inovus.messaging.api.criteria.UserCriteria;
import ru.inovus.messaging.api.criteria.UserSettingCriteria;
import ru.inovus.messaging.api.model.*;
import ru.inovus.messaging.api.model.enums.RecipientType;
import ru.inovus.messaging.api.rest.MessageRest;
import ru.inovus.messaging.api.rest.UserSettingRest;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.impl.UserRoleProvider;
import ru.inovus.messaging.impl.service.ChannelService;
import ru.inovus.messaging.impl.service.MessageService;
import ru.inovus.messaging.impl.service.MessageSettingService;
import ru.inovus.messaging.impl.service.RecipientService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class MessageRestImpl implements MessageRest {
    private final MessageService messageService;
    private final MessageSettingService messageSettingService;
    private final RecipientService recipientService;
    private final ChannelService channelService;
    private final MqProvider mqProvider;
    private final UserSettingRest userSettingRest;
    private final UserRoleProvider userRoleProvider;

    public MessageRestImpl(MessageService messageService,
                           MessageSettingService messageSettingService,
                           RecipientService recipientService,
                           ChannelService channelService,
                           MqProvider mqProvider,
                           UserRoleProvider userRoleProvider,
                           UserSettingRest userSettingRest) {
        this.messageService = messageService;
        this.messageSettingService = messageSettingService;
        this.recipientService = recipientService;
        this.channelService = channelService;
        this.mqProvider = mqProvider;
        this.userRoleProvider = userRoleProvider;
        this.userSettingRest = userSettingRest;
    }

    @Override
    public Page<Message> getMessages(String tenantCode, MessageCriteria criteria) {
        return messageService.getMessages(tenantCode, criteria);
    }

    @Override
    public Message getMessage(String tenantCode, UUID id) {
        Message message = messageService.getMessage(id);
        recipientService.enrichRecipient(message.getRecipients());
        return message;
    }

    @Override
    public void sendMessage(String tenantCode, final MessageOutbox messageOutbox) {
        if (messageOutbox.getMessage() != null) {
            messageOutbox.getMessage().setTenantCode(tenantCode);
            recipientService.enrichRecipient(messageOutbox.getMessage().getRecipients());
            save(messageOutbox.getMessage());
            send(messageOutbox.getMessage());
        } else if (messageOutbox.getTemplateMessageOutbox() != null) {
            messageOutbox.getTemplateMessageOutbox().setTenantCode(tenantCode);
            buildAndSendMessage(messageOutbox.getTemplateMessageOutbox());
        }
    }

    private void buildAndSendMessage(TemplateMessageOutbox templateMessageOutbox) {
        MessageSetting ms = messageSettingService.getSetting(templateMessageOutbox.getTemplateCode());

        if (ms.getDisabled() != null && ms.getDisabled()) {
            return;
        }

        //Пользователи с настройками (из тех, кто должен получить уведомление)
        Map<String, UserSetting> usersWithUserSetting = new HashMap<>();
        //Пользователи без настроек (из тех, кто должен получить уведомление)
        List<String> usersWithDefaultMsgSetting = new ArrayList<>();

        setUsersAndMsgSettings(templateMessageOutbox, usersWithUserSetting, usersWithDefaultMsgSetting);

        //Рассылка пользователям с настройками
        for (Map.Entry<String, UserSetting> entry : usersWithUserSetting.entrySet()) {
            UserSetting userSetting = entry.getValue();
            if (userSetting.getDisabled() == null || !userSetting.getDisabled()) {
                Message message = buildMessage(ms, Collections.singletonList(entry.getKey()), userSetting, templateMessageOutbox);
                save(message);
                send(message);
            }
        }
        //Рассылка пользователям без настроек
        if (!CollectionUtils.isEmpty(usersWithDefaultMsgSetting)) {
            Message message = buildMessage(ms, usersWithDefaultMsgSetting, null, templateMessageOutbox);
            save(message);
            send(message);
        }
    }

    private void save(Message message) {
        Message savedMessage = messageService.createMessage(message, message.getRecipients() == null ? null : message.getRecipients().toArray(new Recipient[0]));
        message.setId(savedMessage.getId());
    }

    private void send(Message message) {
        Channel channel = channelService.getChannel(message.getChannel().getId());
//        mqProvider.publish(constructMessage(message), channel.getQueueName());
    }

    //Заполнение списков Пользователей для рассылки уведомления
    private void setUsersAndMsgSettings(TemplateMessageOutbox templateMessageOutbox, Map<String, UserSetting> usersWithUserSetting, List<String> usersWithDefaultMsgSetting) {
        setUsersAndMsgSettingsByPermissionCodes(templateMessageOutbox, usersWithUserSetting, usersWithDefaultMsgSetting, templateMessageOutbox.getPermissions());
        setUsersAndMsgSettingsByUserNames(templateMessageOutbox, usersWithUserSetting, usersWithDefaultMsgSetting, templateMessageOutbox.getUserNameList());
    }

    //Заполнение списков Пользователей по пермишенам
    private void setUsersAndMsgSettingsByUserNames(TemplateMessageOutbox templateMessageOutbox, Map<String, UserSetting> usersWithUserSetting,
                                                   List<String> usersWithDefaultMsgSetting, List<String> userNames) {
        if (!CollectionUtils.isEmpty(userNames)) {
            for (String userName : userNames) {
                UserSetting userSetting = getUserSetting(templateMessageOutbox, userName);

                if (userSetting.isDefaultSetting()) {
                    usersWithDefaultMsgSetting.add(userName);
                } else {
                    usersWithUserSetting.put(userName, userSetting);
                }
            }
        }
    }

    //Заполнение списков Пользователей по userName
    private void setUsersAndMsgSettingsByPermissionCodes(TemplateMessageOutbox templateMessageOutbox, Map<String, UserSetting> usersWithUserSetting,
                                                         List<String> usersWithDefaultMsgSetting, List<String> permissions) {
        if (!CollectionUtils.isEmpty(permissions)) {
            Set<User> users = getUserByPermissionCodes(permissions);

            if (!CollectionUtils.isEmpty(users)) {
                for (User user : users) {
                    UserSetting userSetting = getUserSetting(templateMessageOutbox, user.getUsername());
                    if (userSetting.isDefaultSetting()) {
                        usersWithDefaultMsgSetting.add(user.getUsername());
                    } else {
                        usersWithUserSetting.put(user.getUsername(), userSetting);
                    }
                }
            }
        }
    }

    //Получение настройки уведомления Пользователя
    private UserSetting getUserSetting(TemplateMessageOutbox templateMessageOutbox, String userName) {
        UserSettingCriteria criteria = new UserSettingCriteria();
        criteria.setPageSize(1);
        criteria.setUsername(userName);
        criteria.setTemplateCode(templateMessageOutbox.getTemplateCode());
        List<UserSetting> userSettingList =
                userSettingRest.getSettings(templateMessageOutbox.getTenantCode(), criteria).getContent();
        return userSettingList.get(0);
    }

    //Построение уведомления по шаблону уведомления и доп. параметрам
    private Message buildMessage(MessageSetting messageSetting, List<String> userNameList, UserSetting userSetting, TemplateMessageOutbox params) {
        Message message = new Message();
        message.setCaption(buildText(messageSetting.getCaption(), params));
        message.setText(buildText(messageSetting.getText(), params));
        message.setSeverity(messageSetting.getSeverity());
        message.setAlertType(userSetting == null ? messageSetting.getAlertType() : userSetting.getAlertType());
        message.setSentAt(params.getSentAt());
        message.setChannel(userSetting == null ? messageSetting.getChannel() : userSetting.getChannel());
        message.setFormationType(messageSetting.getFormationType());
        message.setRecipientType(RecipientType.USER);
        message.setTenantCode(params.getTenantCode());
        message.setRecipients(recipientService.getRecipientsByUsername(userNameList));
        message.setData(null);
        message.setNotificationType(params.getTemplateCode());

        return message;
    }

    //Построение текста уведомления по плейсхолдерам
    private String buildText(String text, TemplateMessageOutbox params) {
        for (Map.Entry<String, Object> placeHolder : params.getPlaceholders().entrySet())
            text = text.replace(placeHolder.getKey(), placeHolder.getValue().toString());
        return text;
    }

    //Получение Пользоватлей по кодам Привилегий
    private Set<User> getUserByPermissionCodes(List<String> permissions) {
        Set<User> users = null;
        Set<Role> roles = getRolesByPermissionCodes(permissions);

        if (!CollectionUtils.isEmpty(roles)) {
            users = getUsersByRoles(roles);
        }

        return users;
    }

    //Полуение Пользователей по Ролям
    private Set<User> getUsersByRoles(Set<Role> roles) {
        UserCriteria userCriteria = new UserCriteria();
        userCriteria.setRoleIds(roles.stream().map(Role::getId).collect(Collectors.toList()));
        userCriteria.setPageNumber(0);

        Page<User> userPage = userRoleProvider.getUsers(userCriteria);
        Set<User> users = new HashSet<>(userPage.getContent());

        if (!CollectionUtils.isEmpty(users) && userPage.getTotalElements() > users.size()) {

            int pageCount = (int) (userPage.getTotalElements() / userCriteria.getPageSize());
            if (userPage.getTotalElements() % userCriteria.getPageSize() != 0) {
                pageCount++;
            }

            for (int i = 1; i < pageCount; i++) {
                userCriteria.setPageNumber(i);
                userPage = userRoleProvider.getUsers(userCriteria);
                users.addAll(userPage.getContent());
            }
        }
        return users;
    }

    //Получение Ролей по кодам Привилегий
    private Set<Role> getRolesByPermissionCodes(List<String> permissions) {
        RoleCriteria roleCriteria = new RoleCriteria();
        roleCriteria.setPermissionCodes(permissions);
        roleCriteria.setPageNumber(0);

        Page<Role> rolePage = userRoleProvider.getRoles(roleCriteria);
        Set<Role> roles = new HashSet<>(rolePage.getContent());

        if (!CollectionUtils.isEmpty(roles) && rolePage.getTotalElements() > roles.size()) {
            int pageCount = (int) (rolePage.getTotalElements() / roleCriteria.getPageSize());
            if (rolePage.getTotalElements() % roleCriteria.getPageSize() != 0) {
                pageCount++;
            }

            for (int i = 1; i < pageCount; i++) {
                roleCriteria.setPageNumber(i);
                rolePage = userRoleProvider.getRoles(roleCriteria);
                roles.addAll(rolePage.getContent());
            }
        }
        return roles;
    }

    private Message constructMessage(Message message) {
        Message newMessage = new Message();
        newMessage.setId(message.getId());
        newMessage.setCaption(message.getCaption());
        newMessage.setText(message.getText());
        newMessage.setSeverity(message.getSeverity());
        newMessage.setRecipients(message.getRecipients());
        newMessage.setTenantCode(message.getTenantCode());
        return newMessage;
    }
}
