package ru.inovus.messaging.impl.service;

import net.n2oapp.platform.test.autoconfigure.pg.EnableEmbeddedPg;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.inovus.messaging.TestApp;
import ru.inovus.messaging.api.model.Channel;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.liquibase.change-log: classpath:/messaging-db/test-base-changelog.xml")
@EnableEmbeddedPg
public class ChannelServiceTest {
    @Autowired
    private ChannelService service;

    @Test
    public void testGetChannels() {
        List<Channel> channels = service.getChannels();
        assertThat(channels.size(), is(3));
        assertThat(channels.get(0).getName(), is("Web"));
        assertThat(channels.get(1).getName(), is("Email"));
        assertThat(channels.get(2).getName(), is("Custom"));
    }

    @Test
    public void testGetChannel() {
        Channel channel = service.getChannel("email");
        assertThat(channel.getName(), is("Email"));
    }
}
