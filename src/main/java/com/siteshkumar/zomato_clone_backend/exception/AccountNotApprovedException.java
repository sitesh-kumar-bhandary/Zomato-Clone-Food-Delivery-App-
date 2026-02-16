package com.siteshkumar.zomato_clone_backend.exception;

public class AccountNotApprovedException extends RuntimeException{
    
    public AccountNotApprovedException(String message){
        super(message);
    }
}
