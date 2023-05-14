package com.lathief.emailscheduling.controller;

import com.lathief.emailscheduling.payload.request.EmailJobRequest;
import com.lathief.emailscheduling.payload.response.EmailJobResponse;
import com.lathief.emailscheduling.utils.EmailJobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@RestController
@RequestMapping("/v1/jobs/email")
public class EmailScheduleController {
    private static final Logger logger = LoggerFactory.getLogger(EmailScheduleController.class);
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private EmailJobBuilder emailJobBuilder;
    @PostMapping
    public ResponseEntity<?> scheduleEmail(@Valid @RequestBody EmailJobRequest emailJobRequest) {
        try {
            ZonedDateTime dateTime = ZonedDateTime.of(emailJobRequest.getDateTime(), emailJobRequest.getTimeZone());
            Map<String, String> s = ZoneId.SHORT_IDS;
            System.out.println(s);
            System.out.println(ZonedDateTime.now());
            if(dateTime.isBefore(ZonedDateTime.now())) {
                EmailJobResponse scheduleEmailResponse = new EmailJobResponse(false,
                        "dateTime must be after current time");
                return ResponseEntity.badRequest().body(scheduleEmailResponse);
            }

            JobDetail jobDetail = emailJobBuilder.buildJobDetail(emailJobRequest);
            Trigger trigger = emailJobBuilder.buildJobTrigger(jobDetail, dateTime);
            scheduler.scheduleJob(jobDetail, trigger);

            EmailJobResponse scheduleEmailResponse = new EmailJobResponse(true,
                    jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Email Scheduled Successfully!");
            return ResponseEntity.ok(scheduleEmailResponse);
        } catch (SchedulerException ex) {
            logger.error("Error scheduling email", ex);

            EmailJobResponse scheduleEmailResponse = new EmailJobResponse(false,
                    "Error scheduling email. Please try later!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(scheduleEmailResponse);
        }
    }
}
