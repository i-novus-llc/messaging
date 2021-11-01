package ru.inovus.messaging.impl.service;

import org.jooq.DSLContext;
import org.jooq.RecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inovus.messaging.api.model.Channel;
import ru.inovus.messaging.impl.jooq.tables.records.ChannelRecord;

import java.util.List;

import static ru.inovus.messaging.impl.jooq.tables.Channel.CHANNEL;

/**
 * Сервис каналов отправки уведомлений
 */
@Service
public class ChannelService {

    @Autowired
    private DSLContext dsl;

    private final RecordMapper<ChannelRecord, Channel> MAPPER = record ->
            new Channel(record.getCode(), record.getName(), record.getQueueName());


    /**
     * Получение списка каналов по коду тенанта
     *
     * @return Список каналов
     */
    public List<Channel> getChannels() {
        return dsl.selectFrom(CHANNEL).fetch(MAPPER);
    }

    /**
     * Получение канала по идентификатору
     *
     * @param code Код канала
     * @return Канал
     */
    public Channel getChannel(String code) {
        return dsl.selectFrom(CHANNEL)
                .where(CHANNEL.CODE.eq(code))
                .fetchOne(MAPPER);
    }
}
