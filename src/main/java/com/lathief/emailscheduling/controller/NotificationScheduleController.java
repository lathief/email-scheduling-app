package com.lathief.emailscheduling.controller;

import com.lathief.emailscheduling.payload.request.PushNotificationRequest;
import com.lathief.emailscheduling.payload.response.EmailJobResponse;
import com.lathief.emailscheduling.payload.response.PushNotificationResponse;
import com.lathief.emailscheduling.service.PushNotificationService;
import com.lathief.emailscheduling.utils.NotificationBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@RestController
@RequestMapping("/v1/jobs/notification")
public class NotificationScheduleController {
    private static final Logger logger = LoggerFactory.getLogger(EmailScheduleController.class);
    @Autowired
    PushNotificationService pushNotificationService;
    @Autowired
    NotificationBuilder notificationBuilder;
    @Autowired
    Scheduler scheduler;
    @PostMapping("/token")
    public ResponseEntity<?> sendTokenNotification(@RequestBody PushNotificationRequest notificationRequest) {
        //pushNotificationService.sendPushNotificationToToken(request);
        //return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
        try {
            ZonedDateTime dateTime = ZonedDateTime.of(notificationRequest.getDateTime(), notificationRequest.getTimeZone());
            Map<String, String> s = ZoneId.SHORT_IDS;
            System.out.println(ZonedDateTime.now());
            if(dateTime.isBefore(ZonedDateTime.now())) {
                PushNotificationResponse pushNotificationResponse = new PushNotificationResponse(0,
                        "dateTime must be after current time");
                return ResponseEntity.badRequest().body(pushNotificationResponse);
            }

            JobDetail jobDetail = notificationBuilder.buildJobDetail(notificationRequest);
            Trigger trigger = notificationBuilder.buildJobTrigger(jobDetail, dateTime);
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
