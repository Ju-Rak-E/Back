package com.example.spring.rmago.service;

//최초 작성자 : 김병훈
//작성일 : 2025-06-01
//Redis를 사용하기 위한 서비스 클래스

import com.example.spring.rmago.dto.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    // RefreshToken 저장 (7일 만료)
    public void saveRefreshToken(String email, String refreshToken, long expirationMs) {
        try {
            String key = "RT:" + email;
            redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(expirationMs));
        } catch (Exception e) {
            throw new RuntimeException("Redis에 RefreshToken 저장 중 오류 발생", e);
        }
    }

    // RefreshToken 조회
    public String getRefreshToken(String email) {
        try {
            return redisTemplate.opsForValue().get("RT:" + email);
        } catch (Exception e) {
            throw new RuntimeException("Redis에서 RefreshToken 조회 중 오류 발생", e);
        }
    }

    // RefreshToken 삭제
    public void deleteRefreshToken(String email) {
        try {
            redisTemplate.delete("RT:" + email);
        } catch (Exception e) {
            throw new RuntimeException("Redis에서 RefreshToken 삭제 중 오류 발생", e);
        }
    }

    // Redis 상태 점검
    public Boolean checkRedisConnection() {
        try {
            return redisTemplate.getConnectionFactory().getConnection().ping().equals("PONG");
        } catch (Exception e) {
            return false;  // Redis가 동작하지 않을 경우 false 반환
        }
    }
}
