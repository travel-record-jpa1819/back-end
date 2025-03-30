package io.travel.map.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAiConfig {


    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder){
        return chatClientBuilder.defaultAdvisors( // 이제 LLM 이 메모리 기능을 활용하여 챗봇 상태에서 대화내용을 기억함
                new MessageChatMemoryAdvisor(new InMemoryChatMemory())).build();

    }
}

