package io.travel.map.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime; // 토큰 만료 시간 (ms 단위)

    // 생성자에서 Key 객체로 변환
    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // JWT 토큰 생성
    public String generateToken(Authentication authentication) {
        String email = authentication.getName();
        log.info("email = {}", email);
        return Jwts.builder()
                .setSubject(email) // sub (JWT의 subject, 사용자 식별자)
                .setIssuedAt(new Date()) // 토큰 생성 시간
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS256)  // HMAC-SHA256 알고리즘으로 서명
                .compact(); //JWT를 compact()를 사용하여 문자열로 변환 후 반환

    }

    // JWT 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // JWT에서 이메일(subject) 추출
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}

