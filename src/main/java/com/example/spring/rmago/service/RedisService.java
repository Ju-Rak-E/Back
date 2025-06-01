package com.example.spring.rmago.service;

//최초 작성자 : 김병훈
//작성일 : 2025-06-01
//Redis를 사용하기 위한 서비스 클래스

import com.example.spring.rmago.dto.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;

    // RefreshToken 저장(7일)
    public void saveRefreshToken(String email, String refreshToken, long expirationMs) {
        String key = "RT:"+ email;
        redisTemplate.opsForValue().set(key,refreshToken, Duration.ofMillis(expirationMs));
    }

    //RefreshToken 조회
    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get("RT:"+ email);
    }

    //RefreshToken삭제
    public void deleteRefreshToken(String email) {
        redisTemplate.delete("RT:"+email);
    }
}
