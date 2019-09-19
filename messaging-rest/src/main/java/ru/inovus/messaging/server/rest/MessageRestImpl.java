package ru.inovus.messaging.server.rest;

import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.rest.api.RoleRestService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestRoleCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.api.MessageSetting;
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.model.*;
import ru.inovus.messaging.api.queue.DestinationResolver;
import ru.inovus.messaging.api.queue.DestinationType;
import ru.inovus.messaging.api.queue.MqProvider;
import ru.inovus.messaging.api.rest.MessageRest;
import ru.inovus.messaging.impl.MessageService;
import ru.inovus.messaging.impl.MessageSettingService;
import ru.inovus.messaging.impl.RecipientService;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MessageRestImpl implements MessageRest {

    private static final Logger log = LoggerFactory.getLogger(MessageRestImpl.class);

    private final MessageService messageService;
    private final MessageSettingService messageSettingService;
    private final RecipientService recipientService;
    private final Long timeout;
    private final MqProvider mqProvider;
    private final String noticeTopicName;
    private final String emailTopicName;
    private final boolean securityAdminRestEnable;
    private final UserRestService userRestService;
    private final RoleRestService roleRestService;
    private DestinationResolver destinationResolver;

    public MessageRestImpl(MessageService messageService,
                           MessageSettingService messageSettingService,
                           RecipientService recipientService,
                           @Value("${novus.messaging.timeout}") Long timeout,
                           @Value("${novus.messaging.topic.notice}") String noticeTopicName,
                           @Value("${novus.messaging.topic.email}") String emailTopicName,
                           @Value("${sec.admin.rest.enable}") boolean securityAdminRestEnable,
                           MqProvider mqProvider,
                           @Qualifier("userRestServiceJaxRsProxyClient") UserRestService userRestService,
                           @Qualifier("roleRestServiceJaxRsProxyClient") RoleRestService roleRestService,
                           DestinationResolver destinationResolver) {
        this.messageService = messageService;
        this.messageSettingService = messageSettingService;
        this.recipientService = recipientService;
        this.timeout = timeout;
        this.mqProvider = mqProvider;
        this.noticeTopicName = noticeTopicName;
        this.emailTopicName = emailTopicName;
        this.securityAdminRestEnable = securityAdminRestEnable;
        this.userRestService = userRestService;
        this.roleRestService = roleRestService;
        this.destinationResolver = destinationResolver;
    }

    @Override
    public Page<Message> getMessages(MessageCriteria criteria) {
        return messageService.getMessages(criteria);
    }

    @Override
    public Message getMessage(String id) {
        Message message = messageService.getMessage(id);
        message.getRecipients().forEach(recipient -> {
            User user = userRestService.getById(Integer.parseInt(recipient.getRecipient()));
            recipient.setName(user.getFio() + " (" + user.getUsername() + ")");
            recipient.setId(user.getId().longValue());
        });

        return message;
    }

    @Override
    public Page<Recipient> getRecipients(RecipientCriteria criteria) {
        Page<Recipient> recipientPage = recipientService.getRecipients(criteria);

        Map<String, String> userMap = new HashMap<>();

        for (Recipient recipient : recipientPage.getContent()) {
            String name = userMap.get(recipient.getRecipient());
            if (name != null) {
                recipient.setRecipient(name);
            } else {
                String userName = recipient.getRecipient();
                RestUserCriteria userCriteria = new RestUserCriteria();
                userCriteria.setSize(1);
                userCriteria.setPage(0);
                userCriteria.setUsername(userName);

                User user = userRestService.findAll(userCriteria).getContent().get(0);
                name = user.getFio() + " (" + user.getUsername() + ")";
                recipient.setName(name);
                userMap.put(userName, name);
            }
        }

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

    private void buildAndSendMessage(TemplateMessageOutbox templateMessageOutbox) {
        MessageSetting ms = getMessageSetting(templateMessageOutbox.getTemplateCode());
        if (!ms.getDisabled()) {
            Message message = buildMessage(ms, templateMessageOutbox);
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

    //Получение шаблона уведомления по коду
    private MessageSetting getMessageSetting(String templateCode) {
        return messageSettingService.getSetting(templateCode);
    }

    //Построение уведомления по шаблону уведомления и доп. параметрам
    private Message buildMessage(MessageSetting messageSetting, TemplateMessageOutbox params) {
        Message message = new Message();
        message.setCaption(buildText(messageSetting.getCaption(), params));
        message.setText(buildText(messageSetting.getText(), params));
        message.setSeverity(messageSetting.getSeverity());
        message.setAlertType(messageSetting.getAlertType());
        message.setSentAt(params.getSendAt());
        message.setInfoTypes(messageSetting.getInfoType());
        message.setComponent(messageSetting.getComponent());
        message.setFormationType(messageSetting.getFormationType());
        message.setRecipientType(RecipientType.USER);
        message.setSystemId(params.getSystemId());
        message.setRecipients(findRecipients(params.getUserNameList(), params.getPermissions()));
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

    //Получение адресатов по ид-р сотрудников и указанным привелегиям
    private List<Recipient> findRecipients(List<String> userNameList, List<String> permissions) {
        Set<Recipient> recipients = new HashSet<>();

        if (!CollectionUtils.isEmpty(userNameList))
            for (String userName : userNameList) {
                Recipient recipient = new Recipient();

                if (securityAdminRestEnable) {
                    RestUserCriteria restUserCriteria = new RestUserCriteria();
                    restUserCriteria.setUsername(userName);

                    User user = userRestService.findAll(restUserCriteria).getContent().get(0);
                    recipient.setEmail(user.getEmail());
                }
                recipient.setRecipient(userName);

                recipients.add(recipient);
            }

        if (securityAdminRestEnable && !CollectionUtils.isEmpty(permissions)) {
            RestRoleCriteria roleCriteria = new RestRoleCriteria();
            roleCriteria.setPermissionCodes(permissions);

            List<Role> roles = roleRestService.findAll(roleCriteria).getContent();

            if (!CollectionUtils.isEmpty(roles)) {
                RestUserCriteria userCriteria = new RestUserCriteria();
                userCriteria.setRoleIds(roles.stream().map(Role::getId).collect(Collectors.toList()));

                List<User> users = userRestService.findAll(userCriteria).getContent();

                for (User user : users) {
                    Recipient recipient = new Recipient();
                    recipient.setEmail(user.getEmail());
                    recipient.setRecipient(user.getUsername());

                    recipients.add(recipient);
                }
            }
        }

        return new ArrayList<>(recipients);
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
