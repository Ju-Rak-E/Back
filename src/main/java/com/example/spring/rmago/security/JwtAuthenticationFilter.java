package com.example.spring.rmago.security;

import com.example.spring.rmago.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;

    public JwtAuthenticationFilter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/customer/login/kakao/android")
                || path.equals("/customer/login/kakao/reissue");
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // HTTP 헤더에서 "Authorization" 키로 JWT를 가져옴
        String token = request.getHeader("Authorization");
//        System.out.println("JWT filter Authorization 키:" + token);
        log.info("JWT filter Authorization 키:" + token);

        if (token != null && token.startsWith("Bearer ")) {
            try {
                // "Bearer " 접두사를 제거하고, JWT만 남겨둠
                token = token.substring(7);

                // JWT 파싱하여 Claims 추출
                SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key) // ✅ SecretKey 사용
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                log.info("JWT파싱 성공(JWT필터) - subject: {}", claims.getSubject());
                // JWT에서 사용자 정보를 추출하여 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(claims.getSubject(), null, null);

                // 인증 객체를 SecurityContext에 저장하여 사용자가 인증됨을 표시
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.error("❌ JWT 파싱 실패: {}", e.getMessage());
                // JWT 파싱 실패 시 예외 처리
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 인증 실패 응답
                response.getWriter().write("Unauthorized");
                return;
            }
        }else{
            log.warn("!!! Authorization 헤더가 없거나 Bearer 토큰 아님: {}", token);
        }

        // 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }
}
