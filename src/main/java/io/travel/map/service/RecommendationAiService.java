package io.travel.map.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import io.travel.map.document.TravelRecommendationDto;
import io.travel.map.document.User;
import io.travel.map.repository.CityRepository;
import io.travel.map.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A service to generate a travel recommendation in JSON format (array)
 * based on the user's visited cities.
 *
 * ChatClient is assumed to be configured with openAI or similar LLM,
 * and we strictly request JSON array output. We then parse it into
 * List<TravelRecommendationDto>.
 */
@Slf4j
@Service
public class RecommendationAiService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationAiService.class);

    private final ChatClient chatClient;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final ObjectMapper objectMapper;

    public RecommendationAiService(ChatClient chatClient,
                                   UserRepository userRepository,
                                   CityRepository cityRepository,
                                   ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 1) Fetch user's visited cities from DB
     * 2) Build a prompt requesting strictly JSON array output (multiple city info)
     * 3) Invoke chatClient -> parse JSON array -> return as List<TravelRecommendationDto>
     */
    public List<TravelRecommendationDto> recommendCitiesBasedOnUser(String email) {
        // 1) Find user or throw
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        // 2) Build a descriptive prompt with visited city info
        String prompt = buildJsonPrompt(user);

        // 3) Call AI
        String rawResponse = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();  // raw string from AI

        // 4) Parse JSON array into List<TravelRecommendationDto>
        try {
            return objectMapper.readValue(
                    rawResponse,
                    new TypeReference<List<TravelRecommendationDto>>() {}
            );
        } catch (Exception e) {
            log.error("Failed to parse AI JSON response: {}", rawResponse, e);
            throw new RuntimeException("Invalid JSON from AI", e);
        }
    }

    /**
     * Helper method to create a prompt that requests an array of recommended cities in valid JSON.
     */
    private String buildJsonPrompt(User user) {
        StringBuilder sb = new StringBuilder();

        sb.append("The user has visited:\n");
        user.getVisitedCities().forEach(visited -> {
            sb.append("- ").append(visited.getCityName()).append(": ");
            sb.append(visited.getNotes() != null ? visited.getNotes() : "No description");
            sb.append(visited.isLiked() ? " (Liked)\n" : " (Not liked)\n");
        });

        // Additional instructions
        sb.append("""
                
                Recommend exactly 6 diverse cities for the user to visit, and make sure the list is different every time this is requested.
                
                Each city should be globally diverse and not repeated from common tourist lists. You can include lesser-known or unique travel destinations.
                
                Return an array of objects (no markdown, no backticks) with these exact fields and a short reason why did you recommend that city.
                Please return a JSON array where all numeric values such as exchangeRateUsd are formatted as plain numbers (e.g., 23500.0 instead of 23,500.0).
                Each element must be:
                {
                  "cityName": "string",
                  "countryAbbreviation": "string",
                  "climate": "string",
                  "exchangeRateUsd": number,
                  "touristSpots": ["string","string","string"]
                  "reason":"string"
                }
                Return only valid JSON array (like [...] ) with no extra text.
                """);
        return sb.toString();
    }
}