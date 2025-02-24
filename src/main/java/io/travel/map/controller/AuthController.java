package io.travel.map.controller;

import io.travel.map.security.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final CustomOAuth2UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(CustomOAuth2UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/login")
    public Map<String, Object> login(OAuth2AuthenticationToken token) {
        // OAuth2 로그인 처리 후 User 객체 가져오기
        User user = userService.processOAuth2User(token);

        // 이메일 가져오기
        String email = (user != null) ? user.getEmail() : extractEmailFromOAuth2Token(token);
        if (email == null) {
            throw new IllegalStateException("이메일 정보를 찾을 수 없습니다.");
        }

        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, token.getAuthorities());

        // JWT 생성
        String jwt = jwtTokenProvider.generateToken(authentication);

        return Map.of("token", jwt, "user", user);
    }

    // OAuth2AuthenticationToken에서 이메일 추출
    private String extractEmailFromOAuth2Token(OAuth2AuthenticationToken token) {
        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        return (String) attributes.get("email");
    }
}

