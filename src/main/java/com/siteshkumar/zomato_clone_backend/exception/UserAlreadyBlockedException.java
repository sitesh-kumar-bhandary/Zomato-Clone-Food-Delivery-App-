package com.siteshkumar.zomato_clone_backend.exception;

public class UserAlreadyBlockedException extends RuntimeException
{
    public UserAlreadyBlockedException(String message){
        super(message);
    }
}
