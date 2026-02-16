package com.siteshkumar.zomato_clone_backend.exception;

public class PhoneAlreadyExistsException extends RuntimeException {

    public PhoneAlreadyExistsException(String message){
        super(message);
    }
}
