package com.Linkdlyst.api_gateway.Utils.GlobalApiResponse;

public class GlobalApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public GlobalApiResponse(boolean _success, String _message, T _data){
        success = _success;
        message = _message;
        data = _data;
    }

    public Boolean getSuccess(){
        return success;
    }
    public String getMessage(){
        return message;
    }
    public T getData(){
        return data;
    }
}
