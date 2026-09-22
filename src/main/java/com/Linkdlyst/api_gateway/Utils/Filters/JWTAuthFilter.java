package com.Linkdlyst.api_gateway.Utils.Filters;

import java.io.IOException;
import java.util.Collections;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.Linkdlyst.api_gateway.Utils.Dtos.JwtContext;
import com.Linkdlyst.api_gateway.Utils.Services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component 
public class JWTAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JWTAuthFilter(JwtService _JwtService){
        jwtService = _JwtService;
    }

    @Override 
    protected void doFilterInternal(
        HttpServletRequest httpRequest,
        HttpServletResponse httpResponse,
        FilterChain filterChain ) throws ServletException, IOException 
        {

        String authHeader = httpRequest.getHeader("Authorization");
        String refreshToken = httpRequest.getHeader("Refresh-Token");

        if(authHeader == null || !authHeader.startsWith("Bearer ") || refreshToken == null){
            filterChain.doFilter(httpRequest, httpResponse);
            return;
        }

        String accessToken = authHeader.substring(7);
        boolean isValidAccessToken = jwtService.isValidToken(accessToken);
        if(!isValidAccessToken){
            filterChain.doFilter(httpRequest, httpResponse);
            return;
        }

        Long userId = jwtService.getUserId(accessToken);
        String userEmail = jwtService.getEmail(accessToken);
        String tokenType = jwtService.getTokenType(accessToken);

        JwtContext jwtContext = new JwtContext(userId, userEmail, tokenType);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            jwtContext,
            null,
            Collections.emptyList()
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(httpRequest, httpResponse);
    }
}
