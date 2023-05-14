package com.lathief.emailscheduling.utils;

import com.lathief.emailscheduling.job.EmailJob;
import com.lathief.emailscheduling.payload.request.EmailJobRequest;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Component
public class EmailJobBuilder {
    public JobDetail buildJobDetail(EmailJobRequest emailJobRequest) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("email", emailJobRequest.getEmail());
        jobDataMap.put("subject", emailJobRequest.getSubject());
        jobDataMap.put("body", emailJobRequest.getBody());

        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                .withDescription("Send Email Job" + emailJobRequest.getEmail())
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    public Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                .withDescription("Send Email Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
