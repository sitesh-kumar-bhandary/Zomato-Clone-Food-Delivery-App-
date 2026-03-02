package com.siteshkumar.zomato_clone_backend.exception;

public class MethodArgumentTypeMismatchException extends RuntimeException {

    public MethodArgumentTypeMismatchException(String message){
        super(message);
    }
}