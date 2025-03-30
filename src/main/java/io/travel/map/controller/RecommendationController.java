package io.travel.map.controller;

import io.travel.map.document.TravelRecommendationDto;
import io.travel.map.service.RecommendationAiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recommend")
public class RecommendationController {

    private final RecommendationAiService aiService;

    public RecommendationController(RecommendationAiService aiService) {
        this.aiService = aiService;
    }

    /**
     * DB 기반 AI 추천, JSON 배열로 파싱된 리스트를 리턴
     * - 토큰의 이메일(또는 User 식별자)을 사용해 유저를 특정
     */
    @GetMapping("/json")
    public ResponseEntity<List<TravelRecommendationDto>> recommendCitiesJson(Authentication authentication) {
        // 1. 인증 여부 체크
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. auth.getName()을 통해 이메일(혹은 username)을 추출
        String email = authentication.getName();

        // 3. 이메일을 기반으로 AI 추천
        //    RecommendationAiService 내부에서 UserRepository 등을 이용해 user 찾기 가능
        List<TravelRecommendationDto> dtoList = aiService.recommendCitiesBasedOnUser(email);

        // 4. 결과 반환
        return ResponseEntity.ok(dtoList);
    }
}
