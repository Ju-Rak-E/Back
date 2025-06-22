package com.example.spring.rmago.security;

// 최초 작성자 : 김병훈
// 작성일 : 2025-06-01
// 토큰의 생성과 리프레쉬 토큰 발급 클래스

import com.example.spring.rmago.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Access Token 발급 (유효시간: 초 단위로 전달)
     */
    public String generateAccessToken(String email, List<String> roles, int expirationSeconds) {
        return generateToken(email, roles, expirationSeconds);
    }

    /**
     * Refresh Token 발급 (roles 없음)
     */
    public String generateRefreshToken(String email, int expirationSeconds) {
        return generateToken(email, null, expirationSeconds);
    }

    /**
     * 공통 JWT 생성 로직
     */
    private String generateToken(String email, List<String> roles, int expirationSeconds) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationSeconds * 1000L);

        JwtBuilder builder = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256);

        if (roles != null && !roles.isEmpty()) {
            builder.claim("roles", roles);
        }

        return builder.compact();
    }

    /**
     * Claims 파싱
     */
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰 유효성 검증
     */
    public TokenStatus validateToken(String token) {
        try {
            getClaims(token); // 파싱 성공
            return TokenStatus.VALID;
        } catch (ExpiredJwtException e) {
            return TokenStatus.EXPIRED;
        } catch (JwtException | IllegalArgumentException e) {
            return TokenStatus.INVALID;
        }
    }

    /**
     * 이메일(subject) 추출
     */
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * roles 추출
     */
    public List<String> getRolesFromToken(String token) {
        Object roles = getClaims(token).get("roles");
        if (roles instanceof List<?> roleList) {
            return roleList.stream().map(Object::toString).toList();
        }
        return List.of(); // 기본 역할 없음
    }

    public String getEmailFromExpiredToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token); // 여기는 무조건 throw
            return null;
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject(); // 만료된 토큰에서도 subject 추출 가능
        }
    }


    /**
     * 만료 여부 확인
     */
    public boolean isTokenExpired(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
