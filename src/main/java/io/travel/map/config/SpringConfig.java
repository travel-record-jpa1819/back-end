package io.travel.map.config;

import io.travel.map.security.JwtAuthFilter;
import io.travel.map.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SpringConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtTokenProvider jwtTokenProvider;

    public SpringConfig(JwtAuthFilter jwtAuthFilter, JwtTokenProvider jwtTokenProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 사용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/auth/login", "/auth/token").permitAll() // 토큰 발급 엔드포인트 추가
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler()) // JWT 발급 로직 추가
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터 위치 수정

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // React 프론트엔드 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Bean
    public AuthenticationSuccessHandler oAuth2SuccessHandler() {
        return (request, response, authentication) -> {
            // OAuth2 로그인 성공 후 JWT 발급
            String jwtToken = generateJwtToken(authentication);

            // JWT를 프론트엔드로 응답 (예: JSON 형태로 반환)
            response.setContentType("application/json");
            response.getWriter().write("{\"token\": \"" + jwtToken + "\"}");
            response.getWriter().flush();
        };
    }

    private String generateJwtToken(Authentication authentication) {
        // JWT 발급 로직 (JwtTokenProvider 사용)
        return jwtTokenProvider.generateToken(authentication);// 실제로 JwtTokenProvider를 이용해 JWT 생성
    }
}



