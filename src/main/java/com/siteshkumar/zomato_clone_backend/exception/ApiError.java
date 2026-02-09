package com.siteshkumar.zomato_clone_backend.exception;

import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import lombok.Data;

@Data
public class ApiError {
    
    private LocalDate timeStamp;
    private String error;
    private HttpStatus statusCode;

    public ApiError(String error, HttpStatus statusCode){
        timeStamp = LocalDate.now();
        this.error = error;
        this.statusCode = statusCode;
    }
}
