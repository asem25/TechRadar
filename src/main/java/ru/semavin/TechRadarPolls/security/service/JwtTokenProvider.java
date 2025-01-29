package ru.semavin.TechRadarPolls.security.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .setAllowedClockSkewSeconds(60)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return !isTokenExpired(claims);
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: " + e.getMessage());
            return false;
        } catch (SecurityException e) {
            log.warn("Invalid JWT signature: " + e.getMessage());
            return false;
        } catch (Exception e) {
            log.warn("Invalid token: " + e.getMessage());
            return false;
        }
    }
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}

