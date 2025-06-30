package com.example.demo.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.InstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.objenesis.strategy.StdInstantiatorStrategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;



public abstract class FileBasedChatMemory implements ChatMemory {

    private final String BASE_DIR;

    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false);
        //定义实例化策略
        kryo.setInstantiatorStrategy((InstantiatorStrategy) new StdInstantiatorStrategy());

    }

    public FileBasedChatMemory(String dir) {
        this.BASE_DIR = dir;
        File baseDir = new File(BASE_DIR);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }

    /**
     * 添加会话消息
     *
     * @param conversationId
     * @param messages
     */
    public void add(String conversationId, List<org.springframework.ai.chat.messages.Message> messages) {
        List<Message> conversationMessages = getOrCreateConversation(conversationId);
        conversationMessages.addAll(messages);
        saveConversation(conversationId, conversationMessages);
    }

    /**
     * @param conversationId
     * @param lastN
     * @return
     */
    public List<Message> get(String conversationId, int lastN) {
        List<Message> conversationMessages = getOrCreateConversation(conversationId);
        return conversationMessages.stream().skip(Math.max(0, conversationMessages.size() - lastN)).toList();
    }

    /**
     * 清空会话消息
     *
     * @param conversationId
     */
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 根据会话ID获取消息记录
     *
     * @param conversationId
     * @return
     */
    private List<Message> getOrCreateConversation(String conversationId) {
        List<Message> message = new ArrayList<>();
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            try (Input input = new Input(new FileInputStream(file))) {
                message = kryo.readObject(input, ArrayList.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return message;
    }

    /**
     * 根据会话ID获取会话文件
     *
     * @param conversationId
     * @return
     */
    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }

    /**
     * 保存会话消息
     *
     * @param conversationId
     * @param messages
     */
    private void saveConversation(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId);
        try (Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, messages);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

