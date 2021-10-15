package ru.inovus.messaging.impl.quartz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.inovus.messaging.api.model.MessageOutbox;
import ru.inovus.messaging.impl.rest.MessageRestImpl;

public class ScheduledMessageJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(ScheduledMessageJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            MessageRestImpl messageService = (MessageRestImpl) context.getScheduler().getContext().get("messageService");
            MessageOutbox messageOutbox = new ObjectMapper().
                    readValue((String) context.getJobDetail().getJobDataMap().get("sms"), MessageOutbox.class);
            messageService.sendMessage(messageOutbox.getMessage().getTenantCode(), messageOutbox);
        } catch (SchedulerException schedulerException) {
            throw new JobExecutionException(schedulerException);
        } catch (JsonProcessingException e) {
            log.warn("Message deserialization exception");
        }
    }
}
