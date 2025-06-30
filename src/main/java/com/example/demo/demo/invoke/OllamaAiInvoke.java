package com.example.demo.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
public class OllamaAiInvoke implements CommandLineRunner {

    @Resource(name = "ollamaChatModel")
    private OllamaChatModel ollamaChatModel;


    @java.lang.Override
    public void run(java.lang.String... args) throws Exception {
        AssistantMessage assistantMessage = ollamaChatModel.call(new Prompt("我是Ollama版本的代码校园")).getResult().getOutput();

    }
}

