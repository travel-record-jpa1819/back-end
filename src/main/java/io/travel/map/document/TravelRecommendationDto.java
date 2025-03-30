package io.travel.map.document;

import lombok.Data;

import java.util.List;

/**
 * AI가 만들어줄 JSON 구조:
 * {
 *   "cityName": "파리",
 *   "climate": "온난습윤",
 *   "exchangeRateUsd": 0.76,
 *   "touristSpots": ["에펠탑","루브르박물관","노트르담대성당"]
 * }
 */
@Data
public class TravelRecommendationDto {
    private String cityName;
    private String countryAbbreviation;
    private String climate;
    private double exchangeRateUsd;
    private List<String> touristSpots;
    private String reason;
}