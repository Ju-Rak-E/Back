package com.example.spring.rmago.config;

import com.example.spring.rmago.security.JwtAuthenticationFilter;
import com.example.spring.rmago.properties.JwtProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProperties jwtProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtProperties);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**", "/v3/api-docs/**",
                                "/health",
                                "/login/**", "/oauth2/**",
                                "/customer/login/kakao/android",
                                "/customer/reissue"
                        ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                        .requestMatchers("/api/taxi/estimate-radius").authenticated() // ✅ 추가
                        .anyRequest().authenticated() // 기타 모든 경로 인증
                )
                .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 설정 (개발 환경에서는 모든 요청 허용)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://192.168.0.33:8080", "http://localhost:8080","http://10.0.2.2:8080"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // 쿠키 포함 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    // Swagger UI에 JWT 인증 기능을 추가하는 설정
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; // 스키마 이름 정의 (Swagger UI에서 표시될 이름)

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) // 모든 API에 이 보안 요구사항 적용
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP) // HTTP 방식의 보안
                                                .scheme("bearer") // Bearer 토큰 사용
                                                .bearerFormat("JWT") // JWT 형식
                                                .description("JWT 토큰을 입력해주세요. (Bearer 접두사 없이)") // 사용자에게 표시될 설명
                                )
                )
                .info(new Info()
                        .title("RMAGO API 문서")
                        .version("1.0")
                        .description("RMAGO 프로젝트의 API 문서입니다. JWT 인증을 통해 접근할 수 있습니다.")
                );
    }
}
