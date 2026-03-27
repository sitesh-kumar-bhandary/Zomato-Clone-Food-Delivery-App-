package com.siteshkumar.zomato_clone_backend.exception;

import java.time.LocalDateTime;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import lombok.Data;

@Data
public class ApiError {

    private LocalDateTime timeStamp;
    private String error;
    private HttpStatus statusCode;
    private String requestId;

    public ApiError(String error, HttpStatus statusCode){
        this.timeStamp = LocalDateTime.now();
        this.error = error;
        this.statusCode = statusCode;
        this.requestId = MDC.get("requestId");
    }
}