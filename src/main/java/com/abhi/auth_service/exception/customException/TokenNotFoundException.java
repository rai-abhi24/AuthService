package com.abhi.auth_service.exception.customException;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String message){
        super(message);
    }
}
