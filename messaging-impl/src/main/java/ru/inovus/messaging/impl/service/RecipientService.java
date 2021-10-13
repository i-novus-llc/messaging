package ru.inovus.messaging.impl.service;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.criteria.UserCriteria;
import ru.inovus.messaging.api.model.FeedCount;
import ru.inovus.messaging.api.model.MessageStatus;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.User;
import ru.inovus.messaging.api.model.enums.MessageStatusType;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.impl.UserRoleProvider;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecipientRecord;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.exists;
import static ru.inovus.messaging.impl.jooq.Tables.*;

/**
 * Сервис получателей уведомлений
 */
@Service
@Slf4j
public class RecipientService {

    private final DSLContext dsl;

    @Autowired
    private FeedService feedService;

    @Autowired
    private MqProvider mqProvider;

    @Autowired
    private UserRoleProvider userRoleProvider;

    @Value("${novus.messaging.queue.feed-count}")
    private String feedCountQueue;

    @Value("${sec.admin.rest.enable}")
    private boolean securityAdminRestEnable;


    private static final RecordMapper<Record, Recipient> MAPPER = rec -> {
        MessageRecipientRecord record = rec.into(MESSAGE_RECIPIENT);
        Recipient recipient = new Recipient();
        recipient.setId(record.getId());
        recipient.setUsername(record.getRecipientUsername());
        recipient.setMessageId(record.getMessageId());
        recipient.setStatusTime(record.getStatusTime());
        recipient.setName(record.getRecipientName());
        recipient.setStatus(record.getStatus());
        recipient.setDeparturedAt(record.getDeparturedAt());
        recipient.setErrorMessage(record.getSendMessageError());
        return recipient;
    };

    public RecipientService(DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * Получение списка получателей уведомлений по критерию
     *
     * @param criteria Критерий получателей
     * @return Список получателей уведомлений
     */
    public Page<Recipient> getRecipients(RecipientCriteria criteria) {
        List<Condition> conditions = new ArrayList<>();
        Optional.ofNullable(criteria.getMessageId())
                .ifPresent(messageId -> conditions.add(MESSAGE_RECIPIENT.MESSAGE_ID.eq(messageId)));
        SelectConditionStep<Record> query = dsl
                .select(MESSAGE_RECIPIENT.fields())
                .from(MESSAGE_RECIPIENT)
                .where(conditions);
        int count = dsl.fetchCount(query);
        List<Recipient> collection = query
                .orderBy(getSortFields(criteria.getSort()))
                .limit(criteria.getPageSize())
                .offset((int) criteria.getOffset())
                .fetch(MAPPER);
        return new PageImpl<>(collection, criteria, count);
    }

    /**
     * Получение списка полей, по которым будет производиться сортировка
     *
     * @param sort Вариант сортировки
     * @return Список полей, по которым будет производиться сортировка
     */
    private Collection<SortField<?>> getSortFields(Sort sort) {
        if (sort.isEmpty())
            return new ArrayList<>();

        return sort.get().map(s -> {
            Field field = MESSAGE_RECIPIENT.field(s.getProperty());
            return (SortField<?>) (s.getDirection().equals(Sort.Direction.ASC) ?
                    field.asc() : field.desc());
        }).collect(Collectors.toList());
    }

    /**
     * Обновление статуса получателя уведомления
     *
     * @param status Статус уведомления
     */
    @Transactional
    public void updateStatus(MessageStatus status) {
        List<Condition> conditions = new ArrayList<>();
        Optional.ofNullable(status.getUsername())
                .ifPresent(username -> conditions.add(MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(username)));
        // изменение статуса, если переход к нему возможен после предыдущего статуса
        // например, нельзя перейти от статуса FAILED к статусу READ
        conditions.add(MESSAGE_RECIPIENT.STATUS.eq(status.getStatus().getPrevStatus()));
        if (status.getMessageId() != null) {
            conditions.add(MESSAGE_RECIPIENT.MESSAGE_ID.eq(UUID.fromString(status.getMessageId())));
            conditions.add(exists(dsl.selectOne().from(MESSAGE)
                    .where(MESSAGE.ID.eq(MESSAGE_RECIPIENT.MESSAGE_ID),
                            MESSAGE.TENANT_CODE.eq(status.getTenantCode()))));
        } else
            conditions.add(exists(dsl.selectOne().from(MESSAGE)
                    .where(MESSAGE.ID.eq(MESSAGE_RECIPIENT.MESSAGE_ID),
                            MESSAGE.TENANT_CODE.eq(status.getTenantCode())
                                    .andExists(dsl.selectOne().from(CHANNEL)
                                            .where(CHANNEL.ID.eq(MESSAGE.CHANNEL_ID)
                                            )))));

        dsl
                .update(MESSAGE_RECIPIENT)
                .set(MESSAGE_RECIPIENT.STATUS, status.getStatus())
                .set(MESSAGE_RECIPIENT.STATUS_TIME, LocalDateTime.now())
                .set(MESSAGE_RECIPIENT.SEND_MESSAGE_ERROR, status.getErrorMessage())
                .where(conditions)
                .execute();

        // отправка количества непрочитанных уведомлений в очередь счетчиков
        if (status.getUsername() != null && MessageStatusType.READ.equals(status.getStatus())) {
            FeedCount feedCount = feedService.getFeedCount(status.getUsername(), status.getTenantCode());
            mqProvider.publish(feedCount, feedCountQueue);
        }
    }

    /**
     * Обогащение получателей уведомлений
     *
     * @param recipients Список получателей уведомлений
     */
    public void enrichRecipient(List<Recipient> recipients) {
        recipients.replaceAll(recipient -> getRecipientByUsername(recipient.getUsername()));
    }

    /**
     * Построение списка получателей уведомлений по списку имен пользователей
     *
     * @param usernameList Список имен пользователей
     * @return Список получателей уведомлений
     */
    public List<Recipient> getRecipientsByUsername(List<String> usernameList) {
        return usernameList.stream().map(this::getRecipientByUsername).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Получение получателя уведомления по имени пользователя
     *
     * @param username Имя пользователя
     * @return Получатель уведомления
     */
    private Recipient getRecipientByUsername(String username) {
        Recipient recipient = new Recipient();
        if (securityAdminRestEnable) {
            UserCriteria userCriteria = new UserCriteria();
            userCriteria.setUsername(username);
            userCriteria.setPageNumber(0);
            userCriteria.setPageSize(1);
            List<ru.inovus.messaging.api.model.User> users = userRoleProvider.getUsers(userCriteria).getContent();
            if (CollectionUtils.isEmpty(users)) {
                log.warn("User with username: {} not found in user provider", username);
                return null;
            } else {
                User user = users.get(0);
                recipient.setName(user.getFio() + " (" + user.getUsername() + ")");
                recipient.setUsername(user.getUsername());
                recipient.setEmail(user.getEmail());
            }
        }
        recipient.setName(username);
        return recipient;
    }
}
