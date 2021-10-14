package ru.inovus.messaging.impl.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.model.Channel;
import ru.inovus.messaging.api.rest.ChannelRest;
import ru.inovus.messaging.impl.service.ChannelService;

import java.util.List;

@Controller
public class ChannelRestImpl implements ChannelRest {

    @Autowired
    private ChannelService channelService;

    @Override
    public List<Channel> getChannels(String tenantCode) {
        return channelService.getChannels(tenantCode);
    }

    @Override
    public Channel getChannel(String tenantCode, Integer id) {
        return channelService.getChannel(id);
    }
}
