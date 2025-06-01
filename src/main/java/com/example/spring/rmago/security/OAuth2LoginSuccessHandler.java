package com.example.spring.rmago.security;

//최초 작성자 : 김병훈
//작성일 : 2025-05-24
//소셜 로그인 성공후 JWT 발급 및 쿠키 저장

import com.example.spring.rmago.properties.JwtProperties;
import com.example.spring.rmago.service.CustomerService;
import com.example.spring.rmago.service.RedisService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final RedisService redisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomerPrincipal customerPrincipal = (CustomerPrincipal) authentication.getPrincipal();
        String email = customerPrincipal.getEmail();

        // ✅ 토큰 발급
        String accessToken = jwtProvider.generateToken(email);
        String refreshToken = jwtProvider.generateRefreshToken(email);

        // ✅ RefreshToken Redis에 저장
        redisService.saveRefreshToken(email, refreshToken, jwtProperties.getExpirationMs() * 7);

        // ✅ 쿠키 생성 (보안옵션은 운영환경 여부에 따라 설정)
        Cookie accessTokenCookie = new Cookie("access_token", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(15 * 60); // 15분
        accessTokenCookie.setSecure(false);   // 로컬 개발은 false

        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        refreshTokenCookie.setSecure(false);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        // ✅ Flutter 앱 딥링크 리다이렉트
        response.sendRedirect("yourapp://login-success");
    }

}