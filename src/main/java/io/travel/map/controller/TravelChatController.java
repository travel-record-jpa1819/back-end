package io.travel.map.controller;

import io.travel.map.service.TravelChatBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class TravelChatController {

    private final TravelChatBotService chatService;


    @PostMapping
    public ResponseEntity<?> chatWithAi(@RequestBody Map<String, String> requestBody) {
        String userMessage = requestBody.get("message");

        if (userMessage == null || userMessage.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message is empty"));
        }

        String aiReply = chatService.askAi(userMessage);

        return ResponseEntity.ok(Map.of(
                "user", userMessage,
                "ai", aiReply
        ));
    }

    @GetMapping("/history")
    public List<String> getChatHistory() {
        return chatService.getConversationList();
    }
}
