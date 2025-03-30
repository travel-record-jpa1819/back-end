package io.travel.map.config;

import io.travel.map.security.JwtAuthFilter;
import io.travel.map.security.JwtTokenProvider;
import io.travel.map.service.CustomOAuth2UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Configuration
public class SpringConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;

    public SpringConfig(JwtAuthFilter jwtAuthFilter, JwtTokenProvider jwtTokenProvider, CustomOAuth2UserService customOAuth2UserService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.jwtTokenProvider = jwtTokenProvider;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/auth/login", "/auth/token").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        // OAuth2 로그인 시 사용자 정보를 가져올 때 CustomOAuth2UserService를 사용하도록 등록
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2SuccessHandler())
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
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
            if (authentication == null || !authentication.isAuthenticated()) {
                log.info("OAuth2 인증 실패");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"OAuth2 인증 실패\"}");
                return;
               // response.sendRedirect("http://localhost:5173/login"); // 배포 코드
            }

           String jwtToken = generateJwtToken(authentication);
            log.info("JWT 발급 완료: {}", jwtToken);

            Cookie jwtCookie = new Cookie("jwt", jwtToken);  //새 쿠키를 생성하여 토큰을 저장
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(7 * 24 * 60 * 60);
            jwtCookie.setAttribute("SameSite", "Lax");

            response.addCookie(jwtCookie); // 쿠키를 클라이언트에 전달
            response.sendRedirect("http://localhost:5173/profile");

            // 백엔드 테스트
//            response.setContentType("application/json");
//            response.getWriter().write("{\"token\": \"" + jwtToken + "\"}");
        };
    }

    private String generateJwtToken(Authentication authentication) {
        return jwtTokenProvider.generateToken(authentication);
    }
}
