package ru.inovus.messaging.impl.rest;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.model.ChannelType;
import ru.inovus.messaging.api.rest.ChannelRest;
import ru.inovus.messaging.channel.api.Channel;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ChannelRestImpl implements ChannelRest {

    @Autowired
    private ListableBeanFactory beanFactory;

    private List<ChannelType> channels;

    @Override
    public List<ChannelType> getChannels() {
        // TODO - сделать более адекватно
        if (channels == null)
            channels = beanFactory.getBeansOfType(Channel.class)
                    .values().stream()
                    .map(ch -> new ChannelType(ch.getType(), ch.getName()))
                    .collect(Collectors.toList());
        return channels;
    }

    @Override
    public ChannelType getChannel(String id) {
        return getChannels().stream()
                .filter(ch -> ch.getId().equals(id))
                .findFirst().orElse(null);
    }
}
