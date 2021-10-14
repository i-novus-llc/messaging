package ru.inovus.messaging.impl.service;

import org.jooq.DSLContext;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Service;
import ru.inovus.messaging.api.model.Channel;
import ru.inovus.messaging.impl.jooq.tables.records.ChannelRecord;

import java.util.List;

import static ru.inovus.messaging.impl.jooq.tables.Channel.CHANNEL;

@Service
public class ChannelService {

    public ChannelService(DSLContext dsl) {
        this.dsl = dsl;
    }

    private final DSLContext dsl;

    private final RecordMapper<ChannelRecord, Channel> MAPPER = record ->
            new Channel(record.getId(), record.getName(), record.getQueueName());

    public List<Channel> getChannels(String tenantCode) {
        return dsl.selectFrom(CHANNEL).where(CHANNEL.TENANT_CODE.eq(tenantCode)).fetch(MAPPER);
    }

    public Channel getChannel(Integer id) {
        return dsl.selectFrom(CHANNEL).where(CHANNEL.ID.eq(id)).fetchOne(MAPPER);
    }
}
