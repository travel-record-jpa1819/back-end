package io.travel.map.controller;

import io.travel.map.document.TravelRecommendationDto;
import io.travel.map.service.RecommendationAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     */
    @GetMapping("/{userId}/json")
    public ResponseEntity<List<TravelRecommendationDto>> recommendCitiesJson(@PathVariable String userId) {
        List<TravelRecommendationDto> dtoList = aiService.recommendCitiesBasedOnUser(userId);
        return ResponseEntity.ok(dtoList);
    }

}
