package io.travel.map.controller;

import io.travel.map.document.User;
import io.travel.map.document.VisitedCity;
import io.travel.map.document.VisitedCityDTO;
import io.travel.map.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
public class VisitedCityController {

    private final UserRepository userRepository;

    public VisitedCityController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * [POST] /api/users/{userId}/visitedCities
     * 특정 유저가 방문한 도시 기록 추가
     */
    @PostMapping("/{userId}/visitedCities")
    public String addVisitedCityForm(
            @PathVariable String userId,
            @ModelAttribute("visitedCityForm") VisitedCityDTO dto // ← form-urlencoded 매핑
    ) {
        // 1. User 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // 2. DTO -> Entity 변환
        VisitedCity newVisited = new VisitedCity();
        newVisited.setCityID(dto.getCityId());
        newVisited.setVisitedAT(dto.getVisitedAt());
        newVisited.setDescription(dto.getDescription());
        newVisited.setPhotos(dto.getPhotos());
        newVisited.setLiked(dto.isLiked());

        // 3. 저장
        user.getVisitedCities().add(newVisited);
        userRepository.save(user);

        // 4. 리다이렉트
        return "redirect:/users/" + userId + "/visitedCities";
    }


    /**
     * [GET] /api/users/{userId}/visitedCities
     * 특정 유저의 방문 도시 리스트 조회
     */
    @GetMapping("/{userId}/visitedCities")
    public ResponseEntity<?> getVisitedCities(@PathVariable String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // visitedCities 리스트 반환
        return ResponseEntity.ok(userOpt.get().getVisitedCities());
    }

}
