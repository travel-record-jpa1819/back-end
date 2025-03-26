package io.travel.map.controller;

import io.travel.map.entity.User;
import io.travel.map.repository.UserRepository;import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ViewController {

    private final UserRepository userRepository;

    public ViewController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile() {
        // JWT AuthFilter가 인증 완료 시, SecurityContext에 Authentication 저장
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            // 인증 실패 시 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        // Authentication 객체에서 이메일 추출 (여기서는 auth.getName()이 이메일로 설정됨)
        String email = auth.getName();

        // DB 조회 (email로 User 찾아오기)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        // JSON 형태로 반환할 데이터 구성
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("photo", user.getPhotoUrl());

        // 200 OK + JSON 응답
        return ResponseEntity.ok(userInfo);
    }
}
