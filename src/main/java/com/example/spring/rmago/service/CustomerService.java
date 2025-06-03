package com.example.spring.rmago.service;

// 최초 작성자 : 김병훈
// 작성일 : 2025-05-24
// 기능 : 카카오 accessToken으로 사용자 정보 조회 및 JWT 발급 처리

import com.example.spring.rmago.dto.TokenResponseDto;
import com.example.spring.rmago.entity.Customer;
import com.example.spring.rmago.properties.JwtProperties;
import com.example.spring.rmago.repository.UserRepository;
import com.example.spring.rmago.security.CustomerPrincipal;
import com.example.spring.rmago.security.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RedisService redisService;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    /**
     * Spring Security OAuth2 로그인 처리용 (WebLogin 전용)
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) kakaoAccount.get("nickname");
        String kakaoId = oAuth2User.getAttribute("id").toString();

        // DB에 사용자 없으면 생성, 있으면 조회
        Customer customer = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    Customer newCustomer = new Customer();
                    newCustomer.setEmail(email);
                    newCustomer.setKakaoId(kakaoId);
                    newCustomer.setNickname(nickname);
                    return userRepository.save(newCustomer);
                });

        return new CustomerPrincipal(customer, oAuth2User.getAttributes());
    }

    /**
     * 안드로이드에서 accessToken을 직접 받아 사용자 정보 조회 후 JWT 발급
     */
    public TokenResponseDto kakaoLogin(String accessToken) {
        Map<String, Object> userInfo = getKakaoUserInfo(accessToken);

        // Kakao 응답에서 필요한 정보 파싱
        String email = (String) ((Map<String, Object>) userInfo.get("kakao_account")).get("email");
        String nickname = (String) ((Map<String, Object>) userInfo.get("properties")).get("nickname");


        // 사용자 정보 DB 저장
        Customer customer = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    Customer newCustomer = new Customer();
                    newCustomer.setEmail(email);
                    newCustomer.setNickname(nickname);
                    newCustomer.setKakaoId(userInfo.get("id").toString());
                    return userRepository.save(newCustomer);
                });

        // JWT 발급
        String access = jwtProvider.generateToken(email);
        String refresh = jwtProvider.generateRefreshToken(email);

        redisService.saveRefreshToken(email, refresh, jwtProperties.getExpirationMs() * 7);

        TokenResponseDto dto = new TokenResponseDto();
        dto.setAccessToken(access);
        dto.setRefreshToken(refresh);

        System.out.println("*** JWT발급 완료: access=" + access + ", refresh=" + refresh);

        return dto;
    }

    public TokenResponseDto reissue(String refreshToken) {
        String email;

        try {
            // refreshToken 복호화 → 사용자 이메일 추출
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtProperties.getSecret().getBytes())
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            email = claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 RefreshToken");
        }

        // 🔹 Redis에서 기존 refreshToken 조회
        String saved = redisService.getRefreshToken(email);
        if (saved == null || !saved.equals(refreshToken)) {
            throw new RuntimeException("RefreshToken이 일치하지 않음");
        }

        // 🔹 새 토큰 생성
        String newAccess = jwtProvider.generateToken(email);
        String newRefresh = jwtProvider.generateRefreshToken(email);

        // 🔹 새 refreshToken을 Redis에 갱신
        redisService.saveRefreshToken(email, newRefresh, jwtProperties.getExpirationMs() * 7);

        // 🔹 응답 DTO 구성
        TokenResponseDto dto = new TokenResponseDto();
        dto.setAccessToken(newAccess);
        dto.setRefreshToken(newRefresh);
        return dto;
    }

    /**
     * Kakao 사용자 정보 조회 (accessToken 기반)
     */
    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        try {
            // 요청 헤더 구성
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            // HttpEntity는 body 없이 헤더만 포함 (GET 방식)
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 요청 실행
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me", // Kakao 사용자 정보 요청 URL
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            System.out.println("👉 Kakao API 호출 accessToken: " + accessToken);
            System.out.println("👉 Kakao 응답: " + response.getBody());

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("카카오 사용자 정보 조회 실패", e);
        }
    }
}
