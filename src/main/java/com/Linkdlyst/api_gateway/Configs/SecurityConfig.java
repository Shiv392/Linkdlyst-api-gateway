package com.Linkdlyst.api_gateway.Configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.Linkdlyst.api_gateway.Utils.Filters.JWTAuthFilter;

@Configuration 
@EnableWebSecurity 
public class SecurityConfig {

    private final JWTAuthFilter jwtAuthFilter;

    public SecurityConfig(JWTAuthFilter _JwtAuthFilter){
        jwtAuthFilter = _JwtAuthFilter;
    }
    
    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.
        csrf(csrf-> csrf.disable())
        .cors(cors-> cors.disable())
        .sessionManagement(session->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth-> auth.
            requestMatchers(
                "/v1/auth/**"
            )
            .permitAll()
            .anyRequest()
            .authenticated()
        )
        .addFilterBefore(
            jwtAuthFilter, 
            UsernamePasswordAuthenticationFilter.class    
        );

        return http.build();
    }
}
