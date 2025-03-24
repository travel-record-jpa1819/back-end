package io.travel.map.controller;

import io.travel.map.service.RecommendationAiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RecommendationController {

    private final RecommendationAiService aiService;

    public RecommendationController(RecommendationAiService aiService) {
        this.aiService = aiService;
    }

    /**
     * 1) 채팅 폼(입력창) 보여주기
     *    - 처음 접속 시에는 입력창만 보여주고, 답변은 없는 상태
     */
    @GetMapping("/chat")
    public String showChatForm() {
        return "chat"; // chat.html 템플릿
    }

    /**
     * 2) 채팅 내용 전송 후 결과 처리
     *    - 사용자가 입력한 메시지를 AI에게 전달하고
     *      그 결과를 동일 템플릿에 표시
     */
    @PostMapping("/chat")
    public String processChatForm(@RequestParam("message") String message, Model model) {
        String aiResponse = aiService.chat(message);
        model.addAttribute("response", aiResponse);
        model.addAttribute("userMessage", message);
        return "chat"; // 다시 chat.html로
    }
}

