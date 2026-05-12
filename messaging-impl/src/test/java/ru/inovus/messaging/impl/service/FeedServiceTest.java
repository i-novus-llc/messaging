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
        // user2: Message6 (RECIPIENT), Message7 (RECIPIENT),
        //        Message8/9/10 (USER_GROUP_BY_ROLE),
        //        Message11 (USER_GROUP_BY_REGION),
        //        Message12 (USER_GROUP_BY_ORGANIZATION)

        // filter by RECIPIENT → Message6, Message7
        FeedCriteria criteria = new FeedCriteria();
        criteria.setRecipientType(RecipientType.RECIPIENT);
        Page<Feed> feed = service.getMessageFeed(TENANT_CODE, USERNAME2, criteria);
        assertThat(feed.getTotalElements(), is(2L));

        // filter by USER_GROUP_BY_ROLE → Message8, Message9, Message10 (sorted by sentAt desc)
        criteria.setRecipientType(RecipientType.USER_GROUP_BY_ROLE);
        feed = service.getMessageFeed(TENANT_CODE, USERNAME2, criteria);
        assertThat(feed.getTotalElements(), is(3L));
        assertThat(feed.getContent().getFirst().getId(), is("33333333-0000-0000-0000-000000000003"));

        // filter by USER_GROUP_BY_REGION → Message11
        criteria.setRecipientType(RecipientType.USER_GROUP_BY_REGION);
        feed = service.getMessageFeed(TENANT_CODE, USERNAME2, criteria);
        assertThat(feed.getTotalElements(), is(1L));
        assertThat(feed.getContent().getFirst().getId(), is("66666666-0000-0000-0000-000000000006"));

        // filter by USER_GROUP_BY_ORGANIZATION → Message12
        criteria.setRecipientType(RecipientType.USER_GROUP_BY_ORGANIZATION);
        feed = service.getMessageFeed(TENANT_CODE, USERNAME2, criteria);
        assertThat(feed.getTotalElements(), is(1L));
        assertThat(feed.getContent().getFirst().getId(), is("77777777-0000-0000-0000-000000000007"));
    }

    @Test
    void testGetFeedStatistics() {
        // user2:
        //   Message6 (SYSTEM/RECIPIENT/SENT),
        //   Message7 (USER/RECIPIENT/SENT),
        //   Message8 (SYSTEM/USER_GROUP_BY_ROLE/SENT),
        //   Message9 (USER/USER_GROUP_BY_ROLE/SCHEDULED),
        //   Message10 (SYSTEM/USER_GROUP_BY_ROLE/FAILED),
        //   Message11 (USER/USER_GROUP_BY_REGION/SENT),
        //   Message12 (SYSTEM/USER_GROUP_BY_ORGANIZATION/SENT)
        FeedStatistics stats = service.getFeedStatistics(TENANT_CODE, USERNAME2);

        assertThat(stats.getTotal(), is(7));
        assertThat(stats.getRead(), is(0));
        // unread = all messages except READ
        assertThat(stats.getUnread(), is(7));
        assertThat(stats.getSystem(), is(4));
        assertThat(stats.getUser(), is(3));
        assertThat(stats.getRecipient(), is(2));
        assertThat(stats.getUserGroupByRole(), is(3));
        assertThat(stats.getUserGroupByRegion(), is(1));
        assertThat(stats.getUserGroupByOrganization(), is(1));
    }

    @Test
    @Transactional
    void testMarkReadAllWithMessageTypeFilter() {
        // user2 has 7 unread: Message6 (SYSTEM/SENT), Message7 (USER/SENT), Message8 (SYSTEM/SENT),
        //                     Message9 (USER/SCHEDULED), Message10 (SYSTEM/FAILED),
        //                     Message11 (USER/SENT), Message12 (SYSTEM/SENT)
        assertThat(service.getFeedCount(TENANT_CODE, USERNAME2).getCount(), is(7));

        // mark only SYSTEM messages as read → Message6, Message8, Message10, Message12 get marked
        FeedCriteria criteria = new FeedCriteria();
        criteria.setMessageType(MessageType.SYSTEM);
        service.markReadAll(TENANT_CODE, USERNAME2, criteria);

        // Message7, Message9, Message11 (all USER) still unread → count = 3
        assertThat(service.getFeedCount(TENANT_CODE, USERNAME2).getCount(), is(3));
    }

    @Test
    @Transactional
    void testMarkReadAllWithRecipientTypeFilter() {
        // user2 has 7 unread: Message6 (RECIPIENT/SENT), Message7 (RECIPIENT/SENT),
        //                     Message8 (USER_GROUP_BY_ROLE/SENT),
        //                     Message9 (USER_GROUP_BY_ROLE/SCHEDULED),
        //                     Message10 (USER_GROUP_BY_ROLE/FAILED),
        //                     Message11 (USER_GROUP_BY_REGION/SENT),
        //                     Message12 (USER_GROUP_BY_ORGANIZATION/SENT)
        assertThat(service.getFeedCount(TENANT_CODE, USERNAME2).getCount(), is(7));

        // mark only RECIPIENT messages as read → Message6 and Message7 get marked
        FeedCriteria criteria = new FeedCriteria();
        criteria.setRecipientType(RecipientType.RECIPIENT);
        service.markReadAll(TENANT_CODE, USERNAME2, criteria);

        // Message8..Message12 (non-RECIPIENT) still unread → count = 5
        assertThat(service.getFeedCount(TENANT_CODE, USERNAME2).getCount(), is(5));
    }
}
