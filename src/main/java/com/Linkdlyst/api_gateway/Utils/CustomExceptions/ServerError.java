package com.Linkdlyst.api_gateway.Utils.CustomExceptions;

public class ServerError extends RuntimeException {
    public ServerError(String message){
        super(message);
    }
}
