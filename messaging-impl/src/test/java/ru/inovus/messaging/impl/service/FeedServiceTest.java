package ru.inovus.messaging.impl.service;

import net.n2oapp.platform.test.autoconfigure.pg.EnableTestcontainersPg;
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
import ru.inovus.messaging.api.model.FeedStatistics;
import ru.inovus.messaging.api.model.enums.MessageType;
import ru.inovus.messaging.api.model.enums.RecipientType;
import ru.inovus.messaging.api.model.enums.Severity;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.liquibase.change-log: classpath:/messaging-db/test-base-changelog.xml")
@EnableTestcontainersPg
class FeedServiceTest {

    @Autowired
    private FeedService service;

    private static final String TENANT_CODE = "tenant3";
    private static final String USERNAME = "user";
    private static final String USERNAME2 = "user2";


    @Test
    void testGetMessageFeed() {
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
        assertThat(feed.getContent().getFirst().getId(), is("340751ba-1065-4ae4-b715-956560263684"));
        assertThat(feed.getContent().getFirst().getSeverity(), is(Severity.INFO));
    }

    @Test
    void test() {
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
        service.markReadAll(TENANT_CODE, USERNAME, new FeedCriteria());
        feedCount = service.getFeedCount(TENANT_CODE, USERNAME);
        assertThat(feedCount.getCount(), is(0));
    }

    @Test
    void testGetMessageFeedByMessageType() {
        // filter by SYSTEM → Message3 (2021-08-15) and Message5 (2021-12-15)
        FeedCriteria criteria = new FeedCriteria();
        criteria.setMessageType(MessageType.SYSTEM);
        Page<Feed> feed = service.getMessageFeed(TENANT_CODE, USERNAME, criteria);
        assertThat(feed.getTotalElements(), is(2L));
        // ordered by sentAt desc
        assertThat(feed.getContent().get(0).getSentAt(), is(LocalDateTime.of(2021, 12, 15, 0, 0, 0)));
        assertThat(feed.getContent().get(1).getSentAt(), is(LocalDateTime.of(2021, 8, 15, 0, 0, 0)));

        // filter by USER → Message4 (2021-10-15)
        criteria.setMessageType(MessageType.USER);
        feed = service.getMessageFeed(TENANT_CODE, USERNAME, criteria);
        assertThat(feed.getTotalElements(), is(1L));
        assertThat(feed.getContent().getFirst().getId(), is("340751ba-1065-4ae4-b715-956560263684"));
        assertThat(feed.getContent().getFirst().getSentAt(), is(LocalDateTime.of(2021, 10, 15, 0, 0, 0)));
    }

    @Test
    void testGetMessageFeedByRecipientType() {
        // user2: Message6 (RECIPIENT), Message7 (RECIPIENT), Message8 (USER_GROUP_BY_ROLE)

        // filter by RECIPIENT → Message6, Message7
        FeedCriteria criteria = new FeedCriteria();
        criteria.setRecipientType(RecipientType.RECIPIENT);
        Page<Feed> feed = service.getMessageFeed(TENANT_CODE, USERNAME2, criteria);
        assertThat(feed.getTotalElements(), is(2L));

        // filter by USER_GROUP_BY_ROLE → Message8
        criteria.setRecipientType(RecipientType.USER_GROUP_BY_ROLE);
        feed = service.getMessageFeed(TENANT_CODE, USERNAME2, criteria);
        assertThat(feed.getTotalElements(), is(1L));
        assertThat(feed.getContent().getFirst().getId(), is("33333333-0000-0000-0000-000000000003"));

        // filter by USER_GROUP_BY_ORGANIZATION → no results
        criteria.setRecipientType(RecipientType.USER_GROUP_BY_ORGANIZATION);
        feed = service.getMessageFeed(TENANT_CODE, USERNAME2, criteria);
        assertThat(feed.getTotalElements(), is(0L));
    }

    @Test
    void testGetFeedStatistics() {
        FeedStatistics stats = service.getFeedStatistics(TENANT_CODE, USERNAME2);

        assertThat(stats.getTotal(), is(3));
        assertThat(stats.getRead(), is(0));
        assertThat(stats.getUnread(), is(3));
        assertThat(stats.getSystem(), is(2));
        assertThat(stats.getUser(), is(1));
        assertThat(stats.getRecipient(), is(2));
        assertThat(stats.getUserGroupByRole(), is(1));
    }

    @Test
    @Transactional
    void testMarkReadAllWithMessageTypeFilter() {
        // user2 has 3 unread: Message6 (SYSTEM), Message7 (USER), Message8 (SYSTEM)
        assertThat(service.getFeedCount(TENANT_CODE, USERNAME2).getCount(), is(3));

        // mark only SYSTEM messages as read → Message6 and Message8 get marked
        FeedCriteria criteria = new FeedCriteria();
        criteria.setMessageType(MessageType.SYSTEM);
        service.markReadAll(TENANT_CODE, USERNAME2, criteria);

        // Message7 (USER) still unread → count = 1
        assertThat(service.getFeedCount(TENANT_CODE, USERNAME2).getCount(), is(1));
    }

    @Test
    @Transactional
    void testMarkReadAllWithRecipientTypeFilter() {
        // user2 has 3 unread: Message6 (RECIPIENT), Message7 (RECIPIENT), Message8 (USER_GROUP_BY_ROLE)
        assertThat(service.getFeedCount(TENANT_CODE, USERNAME2).getCount(), is(3));

        // mark only RECIPIENT messages as read → Message6 and Message7 get marked
        FeedCriteria criteria = new FeedCriteria();
        criteria.setRecipientType(RecipientType.RECIPIENT);
        service.markReadAll(TENANT_CODE, USERNAME2, criteria);

        // Message8 (USER_GROUP_BY_ROLE) still unread → count = 1
        assertThat(service.getFeedCount(TENANT_CODE, USERNAME2).getCount(), is(1));
    }
}
