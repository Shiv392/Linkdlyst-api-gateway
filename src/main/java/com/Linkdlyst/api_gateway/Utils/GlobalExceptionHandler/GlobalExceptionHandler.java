package com.Linkdlyst.api_gateway.Utils.GlobalExceptionHandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.Linkdlyst.api_gateway.Utils.CustomExceptions.ResourceNotFound;
import com.Linkdlyst.api_gateway.Utils.CustomExceptions.ServerError;
import com.Linkdlyst.api_gateway.Utils.CustomExceptions.TooManyRequest;
import com.Linkdlyst.api_gateway.Utils.GlobalApiResponse.GlobalApiResponse;

@RestControllerAdvice 
public class GlobalExceptionHandler {
    @ExceptionHandler(TooManyRequest.class)
    public ResponseEntity<GlobalApiResponse> handleTooManyRequest(TooManyRequest ex){
        return ResponseEntity.status(429)
        .body(
            new GlobalApiResponse(false, ex.getMessage(), null)
        );
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<GlobalApiResponse> handleResourceNotFound(ResourceNotFound ex){
        return ResponseEntity.status(404)
        .body(
            new GlobalApiResponse(false, ex.getMessage(), null)
        );
    }


    @ExceptionHandler(ServerError.class)
    public ResponseEntity<GlobalApiResponse> handleServerError(){
        return ResponseEntity.status(500)
        .body(
            new GlobalApiResponse(false, "Something went wrong", null)
        );
    }
}
