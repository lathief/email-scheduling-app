package com.lathief.emailscheduling.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailJobResponse {
    private boolean success;
    private String jobId;
    private String jobGroup;
    private String message;

    public EmailJobResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public EmailJobResponse(boolean success, String jobId, String jobGroup, String message) {
        this.success = success;
        this.jobId = jobId;
        this.jobGroup = jobGroup;
        this.message = message;
    }
}
