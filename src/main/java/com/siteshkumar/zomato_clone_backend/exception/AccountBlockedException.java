package com.siteshkumar.zomato_clone_backend.exception;

public class AccountBlockedException extends RuntimeException{
    
    public AccountBlockedException(String message){
        super(message);
    }
}
