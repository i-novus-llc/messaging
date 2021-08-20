package ru.inovus.messaging.impl.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.model.ChannelType;
import ru.inovus.messaging.api.rest.ChannelRest;
import ru.inovus.messaging.impl.service.ChannelService;

import java.util.List;

@Controller
public class ChannelRestImpl implements ChannelRest {

    @Autowired
    private ChannelService channelService;

    @Override
    public List<ChannelType> getChannels() {
        return channelService.getChannelTypes();
    }

    @Override
    public ChannelType getChannel(String id) {
        return channelService.getChannelType(id);
    }
}
