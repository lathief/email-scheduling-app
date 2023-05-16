package com.lathief.emailscheduling.utils;

import com.lathief.emailscheduling.job.EmailJob;
import com.lathief.emailscheduling.job.NotificationJob;
import com.lathief.emailscheduling.payload.request.EmailJobRequest;
import com.lathief.emailscheduling.payload.request.PushNotificationRequest;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Component
public class NotificationBuilder {
    public JobDetail buildJobDetail(PushNotificationRequest pushNotificationRequest) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("token", pushNotificationRequest.getToken());
        jobDataMap.put("title", pushNotificationRequest.getTitle());
        jobDataMap.put("body", pushNotificationRequest.getMessage());

        return JobBuilder.newJob(NotificationJob.class)
                .withIdentity(UUID.randomUUID().toString(), "notification-jobs")
                .withDescription("Send Notification Job" + pushNotificationRequest.getToken())
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    public Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "notification-triggers")
                .withDescription("Send notification Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
