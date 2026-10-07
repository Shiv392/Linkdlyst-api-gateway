package com.Linkdlyst.api_gateway.Configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import com.Linkdlyst.api_gateway.Utils.Filters.JWTAuthFilter;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JWTAuthFilter jwtAuthFilter;

    public SecurityConfig(JWTAuthFilter _jwtAuthFilter) {
        jwtAuthFilter = _jwtAuthFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors
                .configurationSource(exchange -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(
                            List.of("http://localhost:4200")
                    );

                    config.setAllowedMethods(
                            List.of(
                                    "GET",
                                    "POST",
                                    "PUT",
                                    "DELETE",
                                    "PATCH",
                                    "OPTIONS"
                            )
                    );

                    config.setAllowedHeaders(
                            List.of("*")
                    );

                    config.setAllowCredentials(true);

                    return config;
                })
                )
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
