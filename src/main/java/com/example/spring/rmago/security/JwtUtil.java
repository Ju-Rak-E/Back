package com.example.spring.rmago.security;

//최초 작성자 : 김병훈
//작성일 : 2025-05-24
//JWT 토큰 생성 유틸리티

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String secret = "yourSecretKey"; // .env나 application.yml에서 분리하는 게 좋음
    private final long accessTokenValidity = 15 * 60 * 1000; // 15분
    private final long refreshTokenValidity = 7 * 24 * 60 * 60 * 1000; // 7일

    public String generateAccessToken(String email) {
        return createToken(email, accessTokenValidity);
    }

    public String generateRefreshToken(String email) {
        return createToken(email, refreshTokenValidity);
    }

    private String createToken(String email, long validity) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}
