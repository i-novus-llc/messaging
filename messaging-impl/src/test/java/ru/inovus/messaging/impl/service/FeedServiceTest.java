package ru.inovus.messaging.impl.service;

import net.n2oapp.platform.test.autoconfigure.pg.EnableEmbeddedPg;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.inovus.messaging.TestApp;
import ru.inovus.messaging.api.criteria.FeedCriteria;
import ru.inovus.messaging.api.model.Feed;
import ru.inovus.messaging.api.model.FeedCount;
import ru.inovus.messaging.api.model.enums.Severity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.liquibase.change-log: classpath:/messaging-db/test-base-changelog.xml")
@EnableEmbeddedPg
public class FeedServiceTest {

    @Autowired
    private FeedService service;

    private final String TENANT_CODE = "tenant3";
    private final String USERNAME = "user";


    @Test
    public void testGetMessageFeed() {
        FeedCriteria criteria = new FeedCriteria();

        Page<Feed> feed = service.getMessageFeed(TENANT_CODE, USERNAME, criteria);
        assertThat(feed.getTotalElements(), is(3L));
        // order by sentAt desc
        assertThat(feed.getContent().get(0).getSentAt(), is(LocalDateTime.of(2021, 12, 15, 0, 0, 0)));
        assertThat(feed.getContent().get(1).getSentAt(), is(LocalDateTime.of(2021, 10, 15, 0, 0, 0)));
        assertThat(feed.getContent().get(2).getSentAt(), is(LocalDateTime.of(2021, 8, 15, 0, 0, 0)));

        // filter by sentAt start
        criteria.setSentAtBegin(LocalDateTime.of(2021, 10, 15, 0, 0, 0));
        feed = service.getMessageFeed(TENANT_CODE, USERNAME, criteria);
        assertThat(feed.getTotalElements(), is(2L));
        assertThat(feed.getContent().get(0).getSentAt(), is(LocalDateTime.of(2021, 12, 15, 0, 0, 0)));
        assertThat(feed.getContent().get(1).getSentAt(), is(LocalDateTime.of(2021, 10, 15, 0, 0, 0)));

        // filter by sentAt end
        criteria = new FeedCriteria();
        criteria.setSentAtEnd(LocalDateTime.of(2021, 10, 15, 0, 0, 0));
        feed = service.getMessageFeed(TENANT_CODE, USERNAME, criteria);
        assertThat(feed.getTotalElements(), is(2L));
        assertThat(feed.getContent().get(0).getSentAt(), is(LocalDateTime.of(2021, 10, 15, 0, 0, 0)));
        assertThat(feed.getContent().get(1).getSentAt(), is(LocalDateTime.of(2021, 8, 15, 0, 0, 0)));

        // filter by severity
        criteria = new FeedCriteria();
        criteria.setSeverity(Severity.INFO);
        feed = service.getMessageFeed(TENANT_CODE, USERNAME, criteria);
        assertThat(feed.getTotalElements(), is(1L));
        assertThat(feed.getContent().get(0).getId(), is("340751ba-1065-4ae4-b715-956560263684"));
        assertThat(feed.getContent().get(0).getSeverity(), is(Severity.INFO));
    }

    @Test
    public void test() {
        FeedCount feedCount = service.getFeedCount(TENANT_CODE, USERNAME);
        assertThat(feedCount.getCount(), is(3));

        // mark read
        Feed feed = service.getMessageAndRead(UUID.fromString("340751ba-1065-4ae4-b715-956560263684"), USERNAME);
        assertThat(feed.getId(), is("340751ba-1065-4ae4-b715-956560263684"));
        assertThat(feed.getText(), is("Message4"));
        assertThat(feed.getSeverity(), is(Severity.INFO));
        assertThat(feed.getSentAt(), is(LocalDateTime.of(2021, 10, 15, 0, 0, 0)));

        feedCount = service.getFeedCount(TENANT_CODE, USERNAME);
        assertThat(feedCount.getCount(), is(2));

        // mark read all
        service.markReadAll(TENANT_CODE, USERNAME);
        feedCount = service.getFeedCount(TENANT_CODE, USERNAME);
        assertThat(feedCount.getCount(), is(0));
    }
}
