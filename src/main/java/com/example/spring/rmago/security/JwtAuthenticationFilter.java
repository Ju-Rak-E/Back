package com.example.spring.rmago.security;

import com.example.spring.rmago.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;

    public JwtAuthenticationFilter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    List<String> excludePaths = List.of(
            "/swagger-ui", "/swagger-ui/", "/swagger-ui/index.html",
            "/v3/api-docs", "/login", "/oauth2",
            "/customer/login/kakao/android", "/customer/reissue"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return excludePaths.stream().anyMatch(path::startsWith);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // HTTP 헤더에서 "Authorization" 키로 JWT를 가져옴
        String token = request.getHeader("Authorization");
//        System.out.println("JWT filter Authorization 키:" + token);
//        log.info("JWT filter Authorization 키:" + token);

        if (token != null && token.startsWith("Bearer ")) {
            try {
                // "Bearer " 접두사를 제거하고, JWT만 남겨둠
                token = token.substring(7);

                // JWT 파싱하여 Claims 추출
                Claims claims = Jwts.parserBuilder()
                        // 비밀 키로 서명 검증
                        .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

                // JWT에서 사용자 정보를 추출하여 인증 객체 생성
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
                // 인증 객체를 SecurityContext에 저장하여 사용자가 인증됨을 표시
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (ExpiredJwtException e) {
                log.warn("만료된 JWT 사용 시도");
                sendErrorResponse(response, "Token expired", HttpServletResponse.SC_UNAUTHORIZED);
                return;

            } catch (Exception e) {
                log.warn("JWT 파싱 실패 또는 변조 감지", e);
                sendErrorResponse(response, "Unauthorized", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }else{
            log.warn("!!! Authorization 헤더가 없거나 Bearer 토큰 아님: {}", token);
        }

        // 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }

    // JSON 형식으로 예외 응답을 전송
    private void sendErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        String json = String.format("{\"status\": %d, \"message\": \"%s\"}", status, message);
        response.getWriter().write(json);
    }
}
