package com.example.demo.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class HappyApp {
    private static String SYSTEM_PROMPT = "扮演情感心理领域的专家，开场像用户表明身份，告知能解决的用户的情况问题。引用用户详述事情经过,对方反应以及自身想法，然后给出解决方案";
    private static  ChatClient chatClient;

    public HappyApp(ChatModel dashscopeChatModel) {
        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel).defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new SimpleLoggerAdvisor()
                )
                .build();

    }

    public String chat(String message, String chatID) {
        ChatResponse response = chatClient.prompt().user(message).advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatID)
                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)).call().chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("chat response: {}", content);
        return content;
    }


    record ActorsFilms(String actor, List<String> film) {}

    /**
     * 报告输出为约定的对象输出
     *@param message
     *@param chatId
     * @return
     */
    public  ActorsFilms chatReport(String message, String chatId) {
        ActorsFilms actorsFilms = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(ActorsFilms.class);
        log.info("ActorsFilms: {}", actorsFilms);
        return actorsFilms;

    }


}
