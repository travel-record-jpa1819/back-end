package io.travel.map.controller;

import io.travel.map.document.User;
import io.travel.map.document.VisitedCityDTO;
import io.travel.map.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;

@Slf4j
@Controller
public class ViewController {

    private final UserRepository userRepository;

    public ViewController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String profile(Model model) {

        // 배포할때는 ResponseEntity<?> 추가, Model model 삭제
        // JWT AuthFilter가 인증 완료 시, SecurityContext에 Authentication 저장
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !auth.isAuthenticated()) {
//            // 인증 실패 시 처리
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
//        }

        // Authentication 객체에서 이메일 추출 (여기서는 auth.getName()이 이메일로 설정됨)
        String email = auth.getName();
        log.info("email:" + new String[]{email});

        // DB 조회 (email로 User 찾아오기)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

//        // JSON 형태로 반환할 데이터 구성 배포용 코드
//        Map<String, Object> userInfo = new HashMap<>();
//        userInfo.put("name", user.getUserName());
//        userInfo.put("email", user.getEmail());
//        userInfo.put("photo", user.getPhotoUrl());
//
//        // 200 OK + JSON 응답
//         return ResponseEntity.ok(userInfo);

        // Thymeleaf에 사용자 정보 전달
        model.addAttribute("userId", user.getId());
        model.addAttribute("name", user.getUserName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("photo", user.getPhotoUrl());

        return "user-profile";

    }
    @GetMapping("/users/{userId}/visitedCityForm")
    public String showVisitedCityForm(@PathVariable String userId, Model model) {
        VisitedCityDTO form = new VisitedCityDTO();
        form.setVisitedAt(LocalDate.now()); // 기본값
        model.addAttribute("userId", userId);
        model.addAttribute("visitedCityForm", form);
        return "visitedCityForm"; // templates/visitedCityForm.html
    }
}


