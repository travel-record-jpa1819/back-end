package io.travel.map.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class CityAbbreviationService {

    private final ChatClient chatClient;

    public CityAbbreviationService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String generateAbbreviation(String cityName) {
        String prompt = String.format("Give me a 2-letter abbreviation for the city '%s'. Respond only with the abbreviation.", cityName);
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return response.trim(); // 예: "NYC"
    }
}
