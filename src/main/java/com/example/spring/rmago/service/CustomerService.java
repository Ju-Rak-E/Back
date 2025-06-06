package com.example.spring.rmago.service;

// 최초 작성자 : 김병훈
// 작성일 : 2025-05-24
// 기능 : 카카오 accessToken으로 사용자 정보 조회 및 JWT 발급 처리

import com.example.spring.rmago.dto.TokenResponseDto;
import com.example.spring.rmago.entity.Customer;
import com.example.spring.rmago.properties.JwtProperties;
import com.example.spring.rmago.repository.UserRepository;
import com.example.spring.rmago.security.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RestTemplate restTemplate;
    private final RedisService redisService;
    private final JwtProperties jwtProperties;

    // 카카오 앱 키를 @Value를 사용하여 가져옵니다.
    @Value("${KAKAO_CLIENT_ID}")
    private String kakaoAppKey;
    @Value("${KAKAO_CLIENT_SECRET}")
    private String kakaoClientSecret;
    @Value("${KAKAO_REDIRECT_URI}")
    private String kakaoRedirectUri;


    /**
     * 안드로이드에서 accessToken을 직접 받아 사용자 정보 조회 후 JWT 발급
     */
    public TokenResponseDto kakaoLoginForAndroid(String accessToken) {
            try {
                Map<String, Object> userInfo = getKakaoUserInfo(accessToken);

                System.out.println("👉 userInfo: " + userInfo); // 전체 JSON 확인

                String email = (String) ((Map<String, Object>) userInfo.get("kakao_account")).get("email");
                String nickname = (String) ((Map<String, Object>) userInfo.get("properties")).get("nickname");

                if (email == null || email.isEmpty()) {
                    throw new RuntimeException("❌ 이메일이 없습니다. 카카오 설정에서 이메일 제공 동의가 되어 있는지 확인하세요.");
                }

                Customer customer = userRepository.findByEmail(email)
                        .orElseGet(() -> {
                            Customer newCustomer = new Customer();
                            newCustomer.setEmail(email);
                            newCustomer.setNickname(nickname);
                            newCustomer.setKakaoId(userInfo.get("id").toString());
                            return userRepository.save(newCustomer);
                        });

                // JWT 발급
                String access = jwtProvider.generateToken(email, 60 * 60);
                String refresh = jwtProvider.generateRefreshToken(email, 60 * 60 * 24 * 7);

                System.out.println("✅ JWT access 발급: " + access);
                System.out.println("✅ JWT refresh 발급: " + refresh);

                TokenResponseDto dto = new TokenResponseDto();
                dto.setAccessToken(access);
                dto.setRefreshToken(refresh);

                return dto;

            } catch (Exception e) {
                System.out.println("❌ 예외 발생: " + e.getMessage());
                throw new RuntimeException("카카오 로그인 처리 중 예외 발생: " + e.getMessage());
            }
        }


        /**
         * 웹에서 authorizationCode로 accessToken을 발급받고 JWT 발급
         */
    public TokenResponseDto kakaoLoginForWeb(String authorizationCode, String redirectUri) {
        // 카카오 OAuth2 인증 코드로 access token 발급
        String accessToken = getAccessTokenFromAuthorizationCode(authorizationCode, redirectUri);
        return kakaoLoginForAndroid(accessToken); // 안드로이드와 동일한 방식으로 처리
    }

    // 카카오 사용자 정보 조회 (accessToken 기반)
    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me"; // 카카오 API 사용자 정보 조회 URL

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken); // Bearer 인증 헤더
        headers.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("카카오 API 호출 실패: " + response.getStatusCode());
            }

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("카카오 사용자 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    // 웹 로그인에서 authorization code로 access token을 발급받는 함수
    private String getAccessTokenFromAuthorizationCode(String authorizationCode, String redirectUri) {
        String url = "https://kauth.kakao.com/oauth/token";
        String params = "grant_type=authorization_code" +
                "&client_id=" + kakaoAppKey +  // 환경변수에서 가져온 카카오 앱 키
                "&client_secret=" + kakaoClientSecret +
                "$redirect_uri=" + kakaoRedirectUri +
                "&code=" + authorizationCode;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> entity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("카카오 accessToken 발급 실패(customerService): " + response.getStatusCode());
            }

            return (String) responseBody.get("access_token");
        } catch (Exception e) {
            throw new RuntimeException("카카오 accessToken 발급에 실패했습니다(customerService): " + e.getMessage());
        }
    }

    /**
     * RefreshToken을 받아 access/refresh 재발급 요청 처리
     */
    public TokenResponseDto reissue(String refreshToken) {
        String email;

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtProperties.getSecret().getBytes())
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            email = claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 RefreshToken");
        }

        // Redis에서 기존 refreshToken 조회
        String saved = redisService.getRefreshToken(email);
        if (saved == null || !saved.equals(refreshToken)) {
            throw new RuntimeException("RefreshToken이 일치하지 않음");
        }

        // 새 토큰 생성 (유효기간 1시간, refreshToken은 7일)
        String newAccess = jwtProvider.generateToken(email, 60 * 60); // 1시간
        String newRefresh = jwtProvider.generateRefreshToken(email, 60 * 60 * 24 * 7); // 7일

        // 새 refreshToken을 Redis에 갱신
        try {
            redisService.saveRefreshToken(email, newRefresh, jwtProperties.getExpirationMs() * 7);
        } catch (Exception e) {
            throw new RuntimeException("Redis 저장 오류: " + e.getMessage());
        }

        // 응답 DTO 구성
        TokenResponseDto dto = new TokenResponseDto();
        dto.setAccessToken(newAccess);
        dto.setRefreshToken(newRefresh);
        return dto;
    }
}
