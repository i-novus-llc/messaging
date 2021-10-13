package ru.inovus.messaging;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.inovus.messaging.api.model.Sms;
import ru.inovus.messaging.impl.rest.MessageRestImpl;
import ru.inovus.messaging.impl.rest.SchedulerRestImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "spring.cloud.consul.config.enabled=false",
                "spring.liquibase.change-log: classpath:/messaging-db/test-base-changelog.xml",
        })
@EnableEmbeddedPg
public class SchedulerRestImplTest {

    @Autowired
    private SchedulerRestImpl schedulerRest;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @MockBean
    MessageRestImpl messageRest;

    @Test
    public void testScheduledMessageJob() throws SchedulerException {
        Sms smsToSend = prepareTestSms();
        LocalDateTime localDateTime = LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS);
        smsToSend.setTimeToSend(localDateTime);
        schedulerRest.createScheduledMessage(smsToSend);

        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(scheduler.getJobGroupNames().get(0))).iterator().next();
        Trigger trigger = scheduler.getTriggersOfJob(jobKey).get(0);
        JobDetailImpl jobDetail = (JobDetailImpl) scheduler.getJobDetail(jobKey);
        assertEquals(new java.sql.Timestamp(trigger.getStartTime().getTime()).toLocalDateTime(), localDateTime);
        assertEquals(jobDetail.getGroup(), "DEFAULT");
        assertEquals(jobDetail.getDescription(), "SMS job for phone number: 999666 | date: " + trigger.getStartTime() + " | templateCode: 666");
    }

    @Test
    public void testInstantlySendMessage() {
        schedulerRest.createScheduledMessage(prepareTestSms());
        verify(messageRest, times(1)).sendMessage(any(), any());
    }

    private Sms prepareTestSms() {
        Sms sms = new Sms();
        sms.setPhoneNumber("999666");
        sms.setTemplateCode("666");
        sms.setPlaceholders(Map.of("placeholder1", "place holder №1 value", "placeholder2", "place holder №2 value"));
        return sms;
    }
}
