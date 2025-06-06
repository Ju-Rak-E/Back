package com.example.spring.rmago.security;

import com.example.spring.rmago.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;

    public JwtAuthenticationFilter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // HTTP 헤더에서 "Authorization" 키로 JWT를 가져옴
        String token = request.getHeader("Authorization");
        System.out.println("JWT filter Authorization 키:" + token);

        if (token != null && token.startsWith("Bearer ")) {
            try {
                // "Bearer " 접두사를 제거하고, JWT만 남겨둠
                token = token.substring(7);

                // JWT 파싱하여 Claims 추출
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(jwtProperties.getSecret()) // 비밀 키로 서명 검증
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                // JWT에서 사용자 정보를 추출하여 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(claims.getSubject(), null, null);

                // 인증 객체를 SecurityContext에 저장하여 사용자가 인증됨을 표시
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // JWT 파싱 실패 시 예외 처리
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 인증 실패 응답
                response.getWriter().write("Unauthorized");
                return;
            }
        }

        // 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }
}
