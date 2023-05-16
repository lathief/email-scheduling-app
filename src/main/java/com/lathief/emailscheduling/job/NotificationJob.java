package com.lathief.emailscheduling.job;

import com.lathief.emailscheduling.payload.request.PushNotificationRequest;
import com.lathief.emailscheduling.service.PushNotificationService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class NotificationJob extends QuartzJobBean {
    @Autowired
    PushNotificationService pushNotificationService;
    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        logger.info("Executing Job with key {}", context.getJobDetail().getKey());

        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String token = jobDataMap.getString("token");
        String body = jobDataMap.getString("body");
        String title = jobDataMap.getString("title");
        PushNotificationRequest request = new PushNotificationRequest(title, body, token);
        pushNotificationService.sendPushNotificationToToken(request);
    }
}
