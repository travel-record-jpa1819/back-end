package io.travel.map.service;

import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RecommendationAiService {
    private static final Logger log = LoggerFactory.getLogger(RecommendationAiService.class);
    private final ChatClient chatClient;

    public RecommendationAiService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String chat(String message) {
            return chatClient.prompt() // 프롬프트 생성
                    .user(message) // 사용자 메세지
                    .call() // AI 모델 호출
                    .content(); // ChatResponse --> 요청정보를 받는 부분(문자열만 필요할때는 content)
        }
}
