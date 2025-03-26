package io.travel.map.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class SpringAiConfig {

    @Value("classpath:/prompt.txt") // resource 에서 변수 값을 불러올때 classpath:/asdad 이런식으로 한다
    private Resource resource;

    // ChatClient<------openAPI key ------> LLM(openai)
    // 이 메서드가 LLM 모델과 연결하여 챗 기능을 사용할 수 있게 함
    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder){
        // LLM 에 역할을 부여한다
        //return chatClientBuilder.defaultSystem("당신은 교육 튜터입니다. " +"개념을 명확하고 간단하게 설명하세요") .build();
        //return chatClientBuilder.defaultSystem(resource).build(); // resource 안에 았는 tone, subject placeHolder 을 가져와서 사용 가능
        // return chatClientBuilder.build();
        return chatClientBuilder.defaultAdvisors( // 이제 LLM 이 메모리 기능을 활용하여 챗봇 상태에서 대화내용을 기억함
                new MessageChatMemoryAdvisor(new InMemoryChatMemory())).build();

    }
}

