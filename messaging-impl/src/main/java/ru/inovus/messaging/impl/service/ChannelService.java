package ru.inovus.messaging.impl.service;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inovus.messaging.api.model.ChannelType;
import ru.inovus.messaging.channel.api.Channel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO - отрефакторить весь класс
@Service
public class ChannelService {
    @Autowired
    private ListableBeanFactory beanFactory;

    private List<ChannelType> channelTypes;
    Map<String, Channel> channels = beanFactory.getBeansOfType(Channel.class);

    public List<ChannelType> getChannels() {
        if (channelTypes == null) {
            channelTypes = channels
                    .values().stream()
                    .map(ch -> new ChannelType(ch.getType(), ch.getName()))
                    .collect(Collectors.toList());
        }
        return channelTypes;
    }

    public ChannelType getChannelType(String id) {
        return getChannels().stream()
                .filter(ch -> ch.getId().equals(id))
                .findFirst().orElse(null);
    }

    public Channel getChannel(String type) {
        return channels.values().stream()
                .filter(ch -> ch.getType().equals(type))
                .findFirst().orElse(null);
    }
}
