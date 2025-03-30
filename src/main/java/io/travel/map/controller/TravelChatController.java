package io.travel.map.controller;

import io.travel.map.service.TravelChatBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class TravelChatController {

    private final TravelChatBotService chatService;

    /**
     * GET /chat
     * - 타임리프 템플릿(chat.html)을 렌더링
     * - 현재까지 대화 목록을 모델에 담아 전달
     */
    @GetMapping
    public String showChatPage(Model model) {
        model.addAttribute("conversation", chatService.getConversationList());
        return "chat";  // -> chat.html
    }

    /**
     * POST /chat
     * - 사용자의 입력 메시지 받음
     * - 서비스 통해 AI 질의 후 답변
     * - 완료 후 다시 GET /chat 으로 리다이렉트
     */
    @PostMapping
    public String handleUserMessage(@RequestParam("userMessage") String userMessage) {
        if (userMessage != null && !userMessage.isBlank()) {
            chatService.askAi(userMessage);
        }
        return "redirect:/chat";
    }
}