package ru.inovus.messaging.channel.web;

import net.n2oapp.framework.config.N2oApplicationBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.inovus.messaging.api.model.FeedCount;
import ru.inovus.messaging.channel.web.configuration.WebChannelProperties;
import ru.inovus.messaging.mq.support.kafka.KafkaMqProvider;

import java.lang.reflect.Type;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "novus.messaging.channel.web.controller=default",
        })
@EmbeddedKafka
@ContextConfiguration(classes = KafkaMqProvider.class)
public class DefaultSpringWebSocketControllerTest extends WebChannelTest {

    @Autowired
    private WebChannelProperties properties;

    @Override
    protected void configureEnvironment(N2oApplicationBuilder builder) {
    }

    @Override
    protected String getTitle(Map map) {
        return ((String) map.get("caption"));
    }

    @Override
    protected Map getPayloadData(Map map) {
        return map;
    }

    @Override
    protected void subscribeFeedCountStompSession(StompSession stompSession) {
        stompSession.subscribe("/user" + properties.getPrivateDestPrefix() + "/" + TENANT_CODE + "/message.count", new DefaultSpringWebSocketTestReceivedMessageHandler());
    }

    @Override
    protected void assertFeedCount(Object receivedFeedCount, FeedCount feedCount) {
        assertThat(receivedFeedCount, is(feedCount.getCount()));
    }

    protected class DefaultSpringWebSocketTestReceivedMessageHandler extends TestReceivedMessageHandler {
        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Integer.class;
        }
    }
}