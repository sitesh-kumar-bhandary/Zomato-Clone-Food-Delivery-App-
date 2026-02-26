package com.siteshkumar.zomato_clone_backend.exception;

public class AddressNotFoundException extends RuntimeException {
    
    public AddressNotFoundException(String message){
        super(message);
    }
}
