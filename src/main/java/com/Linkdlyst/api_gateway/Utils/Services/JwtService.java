package com.Linkdlyst.api_gateway.Utils.Services;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service 
public class JwtService {
    private final SecretKey secretKey;

    public JwtService( @Value("${jwt_secret}") String jwtSecret )
    {
        secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public boolean isValidToken(String token){
        try{
            Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token);

            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    public String getEmail(String token){
        return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
    }

    public Long getUserId(String token){
        return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get("userId", Long.class);
    }

    public String getTokenType(String token){
        return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get("type", String.class);
    }
}
