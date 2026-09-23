package com.Linkdlyst.api_gateway.Configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.Linkdlyst.api_gateway.Utils.Filters.JWTAuthFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JWTAuthFilter jwtAuthFilter;

    public SecurityConfig(JWTAuthFilter _jwtAuthFilter){
        jwtAuthFilter = _jwtAuthFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/auth/**")
                        .permitAll()

                        .anyExchange()
                        .authenticated())
                .addFilterBefore(
                    jwtAuthFilter, 
                    SecurityWebFiltersOrder.AUTHENTICATION    
                )

                .build();
    }
}