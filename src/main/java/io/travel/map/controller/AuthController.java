package io.travel.map.controller;

import io.travel.map.entity.User;
import io.travel.map.security.JwtTokenProvider;
import io.travel.map.service.CustomOAuth2UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
    public ResponseEntity<?> login(OAuth2AuthenticationToken token){
        return ResponseEntity.ok(Map.of("message","Login successful"));

    }

    // OAuth2AuthenticationToken에서 이메일 추출
    private String extractEmailFromOAuth2Token(OAuth2AuthenticationToken token) {
        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        return (String) attributes.get("email");
    }
}

