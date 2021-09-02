package ru.inovus.messaging.impl.service;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inovus.messaging.api.model.ChannelType;
import ru.inovus.messaging.channel.api.Channel;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChannelService {
    @Autowired
    private ListableBeanFactory beanFactory;

    private List<ChannelType> channelTypes;

    private Map<String, Channel> channels;

    @PostConstruct
    public void postConstruct() {
        channels = beanFactory.getBeansOfType(Channel.class);
        channelTypes = channels
                .values().stream()
                .map(ch -> new ChannelType(ch.getType(), ch.getName()))
                .collect(Collectors.toList());
    }

    public List<ChannelType> getChannelTypes() {
        return channelTypes;
    }

    public ChannelType getChannelType(String id) {
        return channelTypes.stream()
                .filter(ch -> ch.getId().equals(id))
                .findFirst().orElse(null);
    }

    public Channel getChannel(String type) {
        return channels.values().stream()
                .filter(ch -> ch.getType().equals(type))
                .findFirst().orElse(null);
    }
}
