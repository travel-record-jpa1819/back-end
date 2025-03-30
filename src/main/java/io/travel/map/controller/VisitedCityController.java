package io.travel.map.controller;

import io.travel.map.document.User;
import io.travel.map.document.VisitedCity;
import io.travel.map.document.VisitedCityDTO;
import io.travel.map.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public class VisitedCityController {

    private final UserRepository userRepository;

    public VisitedCityController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * [POST] /visited-cities
     * - JWT 토큰에서 인증 정보를 가져와서 특정 유저를 식별
     * - 방문한 도시 기록 추가
     */
    @PostMapping("/submit")
    public ResponseEntity<?> addVisitedCityForm(
            @RequestBody VisitedCityDTO dto,
            Authentication authentication // 여기서 JWT 토큰 기반 인증정보를 가져옴
    ) {
        // 1. 인증객체가 없거나 인증되지 않았다면 401
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        // 2. 토큰에서 이메일 추출 (기본적으로 auth.getName()은 UserDetails의 username)
        String email = authentication.getName();

        // 3. DB에서 해당 email 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found by email: " + email));

        // 4. DTO -> Entity 변환
        VisitedCity newVisited = new VisitedCity();
        newVisited.setCityName(dto.getCityName());
        newVisited.setLat(dto.getLat());
        newVisited.setLng(dto.getLng());
        newVisited.setNotes(dto.getNotes());
        newVisited.setLiked(dto.isLiked());

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        newVisited.setDate(LocalDateTime.parse(dto.getDate(), formatter));

        // 5. 유저에게 도시 추가 & 저장
        user.getVisitedCities().add(newVisited);
        userRepository.save(user);

        // 6. 성공 메시지 or 저장된 정보 반환
        return ResponseEntity.ok(Map.of("message", "Visited city added successfully"));
    }

    /**
     * [GET] /visited-cities
     * - JWT 토큰에서 인증 정보를 가져와서 특정 유저를 식별
     * - 해당 유저의 방문 도시 리스트 조회
     */
    @GetMapping("visitedcities")
    public ResponseEntity<?> getVisitedCities(Authentication authentication) {
        // 1. 인증 여부 확인
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        // 2. 토큰에서 이메일 추출
        String email = authentication.getName();

        // 3. 유저 조회
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 4. 해당 유저의 visitedCities 반환
        return ResponseEntity.ok(userOpt.get().getVisitedCities());
    }
}