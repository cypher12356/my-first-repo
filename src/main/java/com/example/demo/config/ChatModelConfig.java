package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Primary;

@Configuration
public class ChatModelConfig {
    @Bean
    @Primary
    public ChatModel primaryChatModel(ChatModel dashscopeChatModel) {
        return dashscopeChatModel;

    }
}

