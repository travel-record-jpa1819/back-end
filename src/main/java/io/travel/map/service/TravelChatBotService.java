package io.travel.map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * - ChatClient 에 이미 InMemoryChatMemory 가 세팅되어 있다고 가정 (SpringAiConfig)
 * - 멀티턴 대화 시, ChatClient 내 memory가 메시지를 누적해서 맥락을 이해
 * - 여기서는 화면에 표시할 전체 대화 목록을 별도로 보관 (conversationList)
 *   (실제로는 DB/Redis 등 영구 저장 권장)
 */
@Service
@RequiredArgsConstructor
public class TravelChatBotService {

    private final ChatClient chatClient;

    // 화면에 표시할 용도의 in-memory 대화 리스트
    private final List<String> conversationList = new ArrayList<>();

    /**
     * 사용자 메시지를 AI에게 전달 후 응답을 conversationList 에 추가
     * @param userMessage 사용자 입력
     * @return AI 답변
     */
    public String askAi(String userMessage) {
        // 1) 사용자 메시지를 리스트에 추가
        conversationList.add("User: " + userMessage);

        // 👉 2~3줄 제한 조건 추가
        String prompt = userMessage + "\n\nPlease answer in 2 to 3 concise sentences. Do not exceed 3 lines.";


        // 2) AI 호출
        ChatResponse response = chatClient.prompt()
                .user(prompt)
                .call() // Spring AI M6 기준
                .chatResponse();  // chatResponse()로 변환

        // 3) AI 응답 텍스트 추출
        String aiReply = response.getResult()
                .getOutput()
                .getText(); // or getContent()

        // 4) 대화 목록에 저장
        conversationList.add("AI: " + aiReply);

        return aiReply;
    }

    /**
     * 현재까지 누적된 대화 목록 반환 (화면 표시용)
     */
    public List<String> getConversationList() {
        return conversationList;
    }
}