package com.abhi.auth_service.exception.customException;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message){
        super(message);
    }
}
