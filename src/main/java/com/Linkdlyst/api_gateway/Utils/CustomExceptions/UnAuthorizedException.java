package com.Linkdlyst.api_gateway.Utils.CustomExceptions;

public class UnAuthorizedException extends RuntimeException {
    public UnAuthorizedException(String message){
        super(message);
    }
}
