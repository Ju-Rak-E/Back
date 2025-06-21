package com.example.spring.rmago.security;

// 최초 작성자 : 김병훈
// 작성일 : 2025-05-24
// 소셜 로그인 성공후 JWT 발급 및 쿠키 저장

import com.example.spring.rmago.properties.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomerPrincipal customerPrincipal = (CustomerPrincipal) authentication.getPrincipal();
        String email = customerPrincipal.getEmail();

        // ✅ 역할 정보 (현재 하나뿐이므로 고정)
        List<String> roles = List.of("ROLE_USER");

        // ✅ 토큰 발급
        String accessToken = jwtProvider.generateAccessToken(email, roles, 60 * 60);        // 1시간
        String refreshToken = jwtProvider.generateRefreshToken(email, 60 * 60 * 24 * 7);    // 7일

        // ✅ 쿠키 생성
        Cookie accessTokenCookie = new Cookie("access_token", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60); // 1시간
        accessTokenCookie.setSecure(false);   // 개발 중은 false

        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 7일
        refreshTokenCookie.setSecure(false);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        // ✅ Flutter 앱 딥링크로 리다이렉트
        response.sendRedirect("yourapp://login-success");
    }

}
