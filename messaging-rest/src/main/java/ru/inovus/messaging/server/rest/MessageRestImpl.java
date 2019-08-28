package ru.inovus.messaging.server.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import ru.i_novus.domrf.lkb.access.api.EmployeeBankService;
import ru.i_novus.domrf.lkb.access.api.EmployeeDomrfService;
import ru.i_novus.domrf.lkb.access.api.model.EmployeeBank;
import ru.i_novus.domrf.lkb.access.api.model.EmployeeDomrf;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.api.MessageSetting;
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.model.InfoType;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.RecipientType;
import ru.inovus.messaging.api.param.MessageParam;
import ru.inovus.messaging.api.queue.DestinationResolver;
import ru.inovus.messaging.api.queue.DestinationType;
import ru.inovus.messaging.api.queue.MqProvider;
import ru.inovus.messaging.api.rest.MessageRest;
import ru.inovus.messaging.impl.MessageService;
import ru.inovus.messaging.impl.MessageSettingService;
import ru.inovus.messaging.impl.RecipientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class MessageRestImpl implements MessageRest {

    private static final Logger log = LoggerFactory.getLogger(MessageRestImpl.class);

    private final MessageService messageService;
    private final MessageSettingService messageSettingService;
    private final EmployeeBankService employeeBankService;
    private final EmployeeDomrfService employeeDomrfService;
    private final RecipientService recipientService;
    private final Long timeout;
    private final MqProvider mqProvider;
    private final String noticeTopicName;
    private final String emailTopicName;
    private DestinationResolver destinationResolver;

    public MessageRestImpl(MessageService messageService,
                           MessageSettingService messageSettingService,
                           @Qualifier("employeeBankServiceJaxRsProxyClient") EmployeeBankService employeeBankService,
                           @Qualifier("employeeDomrfServiceJaxRsProxyClient") EmployeeDomrfService employeeDomrfService,
                           RecipientService recipientService,
                           @Value("${novus.messaging.timeout}") Long timeout,
                           @Value("${novus.messaging.topic.notice}") String noticeTopicName,
                           @Value("${novus.messaging.topic.email}") String emailTopicName,
                           MqProvider mqProvider,
                           DestinationResolver destinationResolver) {
        this.messageService = messageService;
        this.messageSettingService = messageSettingService;
        this.employeeBankService = employeeBankService;
        this.employeeDomrfService = employeeDomrfService;
        this.recipientService = recipientService;
        this.timeout = timeout;
        this.mqProvider = mqProvider;
        this.noticeTopicName = noticeTopicName;
        this.emailTopicName = emailTopicName;
        this.destinationResolver = destinationResolver;
    }

    @Override
    public Page<Message> getMessages(MessageCriteria criteria) {
        return messageService.getMessages(criteria);
    }

    @Override
    public Message getMessage(String id) {
        return messageService.getMessage(id);
    }

    @Override
    public Page<Recipient> getRecipients(RecipientCriteria criteria) {
        return recipientService.getRecipients(criteria);
    }

    @Override
    public void sendMessage(final MessageOutbox messageOutbox) {
        if (messageOutbox.getMessage() != null) {
            save(messageOutbox.getMessage());
            send(messageOutbox.getMessage());
        }
    }

    @Override
    public void buildAndSendMessage(MessageParam params) {
        MessageSetting ms = getMessageSetting(params.getTemplateCode());
        if (!ms.getDisabled()) {
            Message message = buildMessage(ms, params);
            save(message);
            send(message);
        }
    }

    private void save(Message message) {
        Message savedMessage = messageService.createMessage(message, message.getRecipients() == null ? null : message.getRecipients().toArray(new Recipient[0]));
        message.setId(savedMessage.getId());
    }

    private void send(Message message) {
        for (InfoType infoType : message.getInfoTypes())
            mqProvider.publish(new MessageOutbox(message), destinationResolver.resolve(getDestinationMqName(infoType), getDestinationType(infoType)));
    }

    //Получение шаблона уведомления по коду
    private MessageSetting getMessageSetting(String templateCode) {
        return messageSettingService.getSetting(templateCode);
    }

    //Построение уведомления по шаблону уведомления и доп. параметрам
    private Message buildMessage(MessageSetting messageSetting, MessageParam params) {
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
        message.setRecipients(findRecipients(params.getBankEmployeeIdList(), params.getDomrfEmployeeIdList(), params.getPermissions()));
        message.setData(null);
        message.setNotificationType(params.getTemplateCode());
        message.setObjectId(params.getObjectId());
        message.setObjectType(params.getObjectType());

        return message;
    }

    //Построение текста уведомления по плейсхолдерам
    private String buildText(String text, MessageParam params) {
        for (Map.Entry<String, Object> placeHolder : params.getPlaceholders().entrySet())
            text = text.replace(placeHolder.getKey(), placeHolder.getValue().toString());
        return text;
    }

    //Получение адресатов по ид-р сотрудников и указанным привелегиям
    private List<Recipient> findRecipients(List<UUID> bankEmployeeIdList, List<UUID> domrfEmployeeIdList, List<String> permissions) {
        List<Recipient> recipients = new ArrayList<>();

        if (!CollectionUtils.isEmpty(bankEmployeeIdList))
            for (UUID bankEmployeeId : bankEmployeeIdList) {
                EmployeeBank employeeBank = employeeBankService.get(bankEmployeeId);

                Recipient recipient = new Recipient();
                recipient.setName(employeeBank.getUser().getFio());
                recipient.setEmail(employeeBank.getUser().getEmail());

                recipients.add(recipient);
            }

        if (!CollectionUtils.isEmpty(domrfEmployeeIdList))
            for (UUID domrfEmployeeId : domrfEmployeeIdList) {
                EmployeeDomrf employeeDomrf = employeeDomrfService.get(domrfEmployeeId);

                Recipient recipient = new Recipient();
                recipient.setName(employeeDomrf.getUser().getFio());
                recipient.setEmail(employeeDomrf.getUser().getEmail());

                recipients.add(recipient);
            }

        //ToDo реалитзовать метод поиска сотрудников банка/домрф в админке по привелегиям
//        if (!CollectionUtils.isEmpty(permissions))
//            for (String permission : permissions) {
//                List<EmployeeBank> employeeBankList = employeeBankService.findAllByPermission(permission);
//                List<EmployeeDomrf> employeeDomrfList = employeeDomrfService.findAllByPermission(permission);
//
//                for(EmployeeBank employeeBank : employeeBankList){
//                    Recipient recipient = new Recipient();
//                    recipient.setName(employeeBank.getUser().getFio());
//                    recipient.setEmail(employeeBank.getUser().getEmail());
//
//                    recipients.add(recipient);
//                }
//                for(EmployeeDomrf employeDomrf : employeeDomrfList){
//                    Recipient recipient = new Recipient();
//                    recipient.setName(employeDomrf.getUser().getFio());
//                    recipient.setEmail(employeDomrf.getUser().getEmail());
//
//                    recipients.add(recipient);
//                }
//            }

        return recipients;
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
