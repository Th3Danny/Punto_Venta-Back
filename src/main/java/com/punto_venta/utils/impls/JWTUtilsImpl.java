package com.punto_venta.utils.impls;

import com.punto_venta.types.JWTType;
import com.punto_venta.utils.IJWTUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JWTUtilsImpl implements IJWTUtils {
    @Value("${jwt.access-token-secret-key}")
    private String ACCESS_TOKEN_SECRET_KEY;

    @Value("${jwt.access-token-expiration-in-ms}")
    private Long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.refresh-token-secret-key}")
    private String REFRESH_TOKEN_SECRET_KEY;

    @Value("${jwt.refresh-token-expiration-in-ms}")
    private Long REFRESH_TOKEN_EXPIRATION_TIME;


    @Override
    public String generateToken(String email, Map<String, Object> payload, JWTType tokenType) {
        String secretKey = getTokenSecretKey(tokenType);
        Date expirationTime = getTokenExpirationTime(tokenType);

        return Jwts.builder()
                .setSubject(email)
                .addClaims(payload)
                .setExpiration(expirationTime)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    @Override
    public String getSubjectFromToken(String token, JWTType tokenType) {
        String secretKey = getTokenSecretKey(tokenType);

        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public Map<String, Object> getClaimsFromToken(String token, JWTType tokenType) {
        String secretKey = getTokenSecretKey(tokenType);

        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public Boolean isTokenValid(String token, JWTType tokenType) {
        try {
            getClaimsFromToken(token, tokenType);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private String getTokenSecretKey(JWTType type) {
        return switch (type) {
            case ACCESS_TOKEN -> ACCESS_TOKEN_SECRET_KEY;
            case REFRESH_TOKEN -> REFRESH_TOKEN_SECRET_KEY;
        };
    }

    private Date getTokenExpirationTime(JWTType type) {
        return switch (type) {
            case ACCESS_TOKEN -> new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME);
            case REFRESH_TOKEN -> new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME);
        };
    }
}