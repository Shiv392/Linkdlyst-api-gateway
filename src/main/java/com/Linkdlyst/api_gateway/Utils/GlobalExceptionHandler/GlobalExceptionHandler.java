package com.Linkdlyst.api_gateway.Utils.GlobalExceptionHandler;

import java.nio.charset.StandardCharsets;

import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import com.Linkdlyst.api_gateway.Utils.CustomExceptions.UnAuthorizedException;
import com.Linkdlyst.api_gateway.Utils.GlobalApiResponse.GlobalApiResponse;

import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Component 
@Order(-2)
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override 
    public Mono<Void> handle(
        ServerWebExchange exchange,
        Throwable ex
    ){
        HttpStatus status;
        String message;

        if(ex instanceof  UnAuthorizedException){
            status = HttpStatus.UNAUTHORIZED;
            message = ex.getMessage();
        }
        else{
        return Mono.error(ex);
        }

        return writeResponse(exchange, status, message);
    }

    private Mono<Void> writeResponse(
        ServerWebExchange exchange,
        HttpStatus status,
        String message) {

        GlobalApiResponse<Void> response =
                new GlobalApiResponse<>(
                        false,
                        message,
                        null
                );

        try {

            String json =
                    objectMapper.writeValueAsString(response);

            exchange.getResponse().setStatusCode(status);

            exchange.getResponse()
                    .getHeaders()
                    .setContentType(MediaType.APPLICATION_JSON);

            DataBuffer buffer =
                    exchange.getResponse()
                            .bufferFactory()
                            .wrap(json.getBytes(StandardCharsets.UTF_8));

            return exchange.getResponse()
                    .writeWith(Mono.just(buffer));

        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
