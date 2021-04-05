package ru.inovus.messaging.server.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.model.MessageOutbox;
import ru.inovus.messaging.api.model.Sms;
import ru.inovus.messaging.api.model.TemplateMessageOutbox;
import ru.inovus.messaging.api.rest.SchedulerRest;
import ru.inovus.messaging.server.quartz.ScheduledMessageJob;

import java.time.LocalDateTime;
import java.time.OffsetTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Controller
public class SchedulerRestImpl implements SchedulerRest {

    private static final Logger log = LoggerFactory.getLogger(SchedulerRestImpl.class);

    private Scheduler scheduler;

    public SchedulerRestImpl(SchedulerFactoryBean schedulerFactoryBean, MessageRestImpl messageService) throws SchedulerException {
        this.scheduler = schedulerFactoryBean.getScheduler();
        scheduler.getContext().put("messageService", messageService);
        scheduler.start();
    }

    @Override
    public void createScheduledMessage(Sms sms) throws JsonProcessingException {
        TemplateMessageOutbox templateMessageOutbox = new TemplateMessageOutbox();
        templateMessageOutbox.setPhoneNumber(sms.getPhoneNumber());
        templateMessageOutbox.setTemplateCode(sms.getTemplateCode());
        templateMessageOutbox.setPlaceholders(sms.getPlaceholders());

        MessageOutbox messageOutbox = new MessageOutbox();
        messageOutbox.setTemplateMessageOutbox(templateMessageOutbox);

        TriggerBuilder tB = TriggerBuilder.newTrigger();
        tB = sms.getSendImmediately() ? tB.startNow() : tB.startAt(convertToDate(sms.getTimeToSend()));
        Trigger trigger = tB.build();

        JobDetail jobDetail = JobBuilder.newJob(ScheduledMessageJob.class).withIdentity(UUID.randomUUID().toString())
                .withDescription(messageDescription(sms, trigger.getStartTime())).build();
        ObjectMapper mapper = new ObjectMapper();
        jobDetail.getJobDataMap().put("sms", mapper.writeValueAsString(messageOutbox));
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException exception) {
            log.warn("Error with ScheduledMessageJob scheduling", exception);
        }
    }

    private String messageDescription(Sms sms, Date date) {
        StringBuilder builder = new StringBuilder("SMS job for phone number: ");
        builder.append(sms.getPhoneNumber()).append(" | date: ")
                .append(date).append(" | templateCode: ")
                .append(sms.getTemplateCode());
        return builder.toString();
    }

    private Date convertToDate(LocalDateTime dateToConvert) {
        return Date.from(dateToConvert.toInstant(OffsetTime.now().getOffset()));
    }
}
