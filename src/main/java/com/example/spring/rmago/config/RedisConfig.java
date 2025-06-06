package com.example.spring.rmago.config;

//최초 작성자: 김병훈
//최초 작성일: 2025-06-05
//Redis 연결 팩토리와 RedisSeriallizer를 설정하여 Redis 서버와의 연결을 맞추고,
//데이터를 읽고 쓸 때 사용할 직렬화 방식을 설정

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    //RedisTemplate 설정(기본적으로 String 타입만 다루는 경우)
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory)  {
        RedisTemplate<String,String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //Redis에서 String 값을 다룰 수 있도록 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        return redisTemplate;
    }

    //StringRedisTemplate 설정(String 값만 다를 경우 간단히 설정 가능)
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

}
