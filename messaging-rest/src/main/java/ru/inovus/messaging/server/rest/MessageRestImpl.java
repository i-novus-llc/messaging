package ru.inovus.messaging.server.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.api.MessageSetting;
import ru.inovus.messaging.api.criteria.*;
import ru.inovus.messaging.api.model.*;
import ru.inovus.messaging.api.queue.DestinationResolver;
import ru.inovus.messaging.api.queue.DestinationType;
import ru.inovus.messaging.api.queue.MqProvider;
import ru.inovus.messaging.api.rest.MessageRest;
import ru.inovus.messaging.api.rest.UserSettingRest;
import ru.inovus.messaging.impl.MessageService;
import ru.inovus.messaging.impl.MessageSettingService;
import ru.inovus.messaging.impl.RecipientService;
import ru.inovus.messaging.impl.UserRoleProvider;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class MessageRestImpl implements MessageRest {
    private final MessageService messageService;
    private final MessageSettingService messageSettingService;
    private final RecipientService recipientService;
    private final MqProvider mqProvider;
    private final String noticeTopicName;
    private final String emailTopicName;
    private final boolean securityAdminRestEnable;
    private DestinationResolver destinationResolver;
    private final UserSettingRest userSettingRest;
    private final UserRoleProvider userRoleProvider;

    public MessageRestImpl(MessageService messageService,
                           MessageSettingService messageSettingService,
                           RecipientService recipientService,
                           @Value("${novus.messaging.topic.notice}") String noticeTopicName,
                           @Value("${novus.messaging.topic.email}") String emailTopicName,
                           @Value("${sec.admin.rest.enable}") boolean securityAdminRestEnable,
                           MqProvider mqProvider,
                           UserRoleProvider userRoleProvider,
                           DestinationResolver destinationResolver,
                           UserSettingRest userSettingRest) {
        this.messageService = messageService;
        this.messageSettingService = messageSettingService;
        this.recipientService = recipientService;
        this.mqProvider = mqProvider;
        this.noticeTopicName = noticeTopicName;
        this.emailTopicName = emailTopicName;
        this.securityAdminRestEnable = securityAdminRestEnable;
        this.userRoleProvider = userRoleProvider;
        this.destinationResolver = destinationResolver;
        this.userSettingRest = userSettingRest;
    }

    @Override
    public Page<Message> getMessages(MessageCriteria criteria) {
        return messageService.getMessages(criteria);
    }

    @Override
    public Message getMessage(UUID id) {
        Message message = messageService.getMessage(id);
        enrichRecipientName(message.getRecipients());
        return message;
    }

    @Override
    public Page<Recipient> getRecipients(RecipientCriteria criteria) {
        Page<Recipient> recipientPage = recipientService.getRecipients(criteria);
        enrichRecipientName(recipientPage.getContent());
        return recipientPage;
    }

    @Override
    public void sendMessage(final MessageOutbox messageOutbox) {
        if (messageOutbox.getMessage() != null) {
            save(messageOutbox.getMessage());
            send(messageOutbox.getMessage());
        } else if (messageOutbox.getTemplateMessageOutbox() != null)
            buildAndSendMessage(messageOutbox.getTemplateMessageOutbox());
    }

    private void enrichRecipientName(List<Recipient> recipients) {

        if (CollectionUtils.isEmpty(recipients)) {
            return;
        }

        Map<String, String> userMap = new HashMap<>();

        for (Recipient recipient : recipients) {
            String userName = recipient.getRecipient();
            String recipientName = userMap.get(userName);
            if (recipientName != null) {
                recipient.setRecipient(recipientName);
            } else {
                UserCriteria userCriteria = new UserCriteria();
                userCriteria.setUsername(recipient.getRecipient());
                userCriteria.setPageSize(1);
                userCriteria.setPageNumber(0);

                Page<User> userPage = userRoleProvider.getUsers(userCriteria);
                List<User> userList = userPage.getContent();
                if (!CollectionUtils.isEmpty(userList)) {
                    User user = userList.get(0);
                    recipientName = user.getFio() + " (" + user.getUsername() + ")";
                    userMap.put(userName, recipientName);
                    recipient.setName(recipientName);
                }
            }
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

        //Рассылка пользоватлеям с настройками
        for (Map.Entry<String, UserSetting> entry : usersWithUserSetting.entrySet()) {
            UserSetting userSetting = entry.getValue();
            if (userSetting.getDisabled() == null || !userSetting.getDisabled()) {
                Message message = buildMessage(ms, Collections.singletonList(entry.getKey()), userSetting, templateMessageOutbox);
                save(message);
                send(message);
            }
        }
        //Рассылка пользоватлеям без настроек
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
        for (InfoType infoType : message.getInfoTypes()) {
            if (infoType == InfoType.NOTICE || (infoType == InfoType.EMAIL && securityAdminRestEnable)) {
                mqProvider.publish(new MessageOutbox(message), destinationResolver.resolve(getDestinationMqName(infoType), getDestinationType(infoType)));
            }
        }
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
        criteria.setUser(userName);
        criteria.setTemplateCode(templateMessageOutbox.getTemplateCode());
        List<UserSetting> userSettingList = userSettingRest.getSettings(criteria).getContent();
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
        message.setInfoTypes(userSetting == null ? messageSetting.getInfoType() : userSetting.getInfoTypes());
        message.setComponent(messageSetting.getComponent());
        message.setFormationType(messageSetting.getFormationType());
        message.setRecipientType(RecipientType.USER);
        message.setSystemId(params.getSystemId());
        message.setRecipients(getRecipientByUserName(userNameList));
        message.setData(null);
        message.setNotificationType(params.getTemplateCode());
        message.setObjectId(params.getObjectId());
        message.setObjectType(params.getObjectType());

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

    //Построение списка Получателей уведомления по списку userName Пользователей
    private List<Recipient> getRecipientByUserName(List<String> userNameList) {
        return userNameList.stream().map(this::getRecipientByUserName).collect(Collectors.toList());
    }

    //Построение Получателя уведомления по userName Пользователя
    private Recipient getRecipientByUserName(String userName) {
        Recipient recipient = new Recipient();

        if (securityAdminRestEnable) {
            UserCriteria userCriteria = new UserCriteria();
            userCriteria.setUsername(userName);
            userCriteria.setPageNumber(0);
            userCriteria.setPageSize(1);

            User user = userRoleProvider.getUsers(userCriteria).getContent().get(0);
            recipient.setEmail(user.getEmail());
        }
        recipient.setRecipient(userName);

        return recipient;
    }

    private DestinationType getDestinationType(InfoType infoType) {
        switch (infoType) {
            case EMAIL:
                return DestinationType.CONSUMER;
            case NOTICE:
                return DestinationType.SUBSCRIBER;
            default:
                return null;
        }
    }

    private String getDestinationMqName(InfoType infoType) {
        switch (infoType) {
            case EMAIL:
                return emailTopicName;
            case NOTICE:
                return noticeTopicName;
            default:
                return null;
        }
    }
}
