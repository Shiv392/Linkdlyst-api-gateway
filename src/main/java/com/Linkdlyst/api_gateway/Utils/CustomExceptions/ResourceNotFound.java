package com.Linkdlyst.api_gateway.Utils.CustomExceptions;

public class ResourceNotFound extends RuntimeException {
    public ResourceNotFound(String message){
        super(message);
    }
}
