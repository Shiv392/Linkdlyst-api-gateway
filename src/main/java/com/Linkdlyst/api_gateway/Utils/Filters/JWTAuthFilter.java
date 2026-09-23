package com.Linkdlyst.api_gateway.Utils.Filters;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.Linkdlyst.api_gateway.Utils.Dtos.JwtContext;
import com.Linkdlyst.api_gateway.Utils.Services.JwtService;
import reactor.core.publisher.Mono;


@Component
public class JWTAuthFilter implements WebFilter {

    private final JwtService jwtService;
    private final Logger logger = LoggerFactory.getLogger(JWTAuthFilter.class);

    public JWTAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            WebFilterChain chain) {

        String authHeader = exchange
                .getRequest()
                .getHeaders()
                .getFirst("Authorization");
        logger.info("Auth Header: "+authHeader);

        // Token nahi hai
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            return chain.filter(exchange);
        }

        String accessToken = authHeader.substring(7);
        // Token invalid hai
        if (!jwtService.isValidToken(accessToken)) {
            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        // Token valid hai
        Long userId = jwtService.getUserId(accessToken);
        String userEmail = jwtService.getEmail(accessToken);
        String tokenType = jwtService.getTokenType(accessToken);

        JwtContext jwtContext =
                new JwtContext(
                        userId,
                        userEmail,
                        tokenType
                );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        jwtContext,
                        null,
                        Collections.emptyList()
                );

        return chain
                .filter(exchange)
                .contextWrite(
                        ReactiveSecurityContextHolder
                                .withAuthentication(authentication)
                );
    }
}