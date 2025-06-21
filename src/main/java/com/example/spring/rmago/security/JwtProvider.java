package com.example.spring.rmago.security;

// 최초 작성자 : 김병훈
// 작성일 : 2025-06-01
// 토큰의 생성과 리프레쉬 토큰 발급 클래스

import com.example.spring.rmago.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    // SecretKey 객체로 변환
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    // AccessToken 발급 (유효 기간을 파라미터로 받음)
    public String generateToken(String subject, int expirationInSeconds) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationInSeconds * 1000); // 유효 기간을 초 단위로 설정

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // RefreshToken 발급 (유효 기간을 파라미터로 받음)
    public String generateRefreshToken(String subject, int expirationInSeconds) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationInSeconds * 1000); // 유효 기간을 초 단위로 설정

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // subject(email) 추출
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
