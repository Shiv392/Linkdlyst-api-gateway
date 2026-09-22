package com.Linkdlyst.api_gateway.Utils.CustomExceptions;

public class TooManyRequest extends RuntimeException {
    public TooManyRequest(String message){
        super(message);
    }
}
