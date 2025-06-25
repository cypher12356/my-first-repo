package com.example.demo;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 阿里云DashScope API客户端
 * 用于与通义千问大模型进行交互
 */
@Slf4j
@Component
public class DashScopeClient {

    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    @Value("${dashscope.api-key:}")
    private String apiKey;

    /**
     * 发送消息到通义千问大模型并获取回复
     *
     * @param messages 消息列表
     * @param model 模型名称，默认为qwen-plus
     * @return 模型回复
     */
    public String chat(List<Message> messages, String model) {
        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", model != null ? model : "qwen-plus");

        // 构建input部分
        JSONObject input = new JSONObject();
        JSONArray messagesArray = new JSONArray();

        // 添加消息
        for (Message message : messages) {
            JSONObject messageObj = new JSONObject();
            messageObj.set("role", message.getRole());
            messageObj.set("content", message.getContent());
            messagesArray.add(messageObj);
        }

        input.set("messages", messagesArray);
        requestBody.set("input", input);

        // 构建parameters部分
        JSONObject parameters = new JSONObject();
        parameters.set("result_format", "message");
        requestBody.set("parameters", parameters);

        // 发送请求
        HttpResponse response = HttpRequest.post(API_URL)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .execute();

        // 处理响应
        String responseBody = response.body();
        log.info("DashScope API response: {}", responseBody);

        // 解析响应
        JSONObject responseJson = JSONUtil.parseObj(responseBody);
        if (response.isOk()) {
            JSONObject output = responseJson.getJSONObject("output");
            if (output != null) {
                JSONObject choices = output.getJSONArray("choices").getJSONObject(0);
                JSONObject message = choices.getJSONObject("message");
                return message.getStr("content");
            }
        }

        // 处理错误
        throw new RuntimeException("调用DashScope API失败: " + responseJson.getStr("message", "未知错误"));
    }

    /**
     * 发送消息到通义千问大模型并获取回复（使用默认模型）
     *
     * @param messages 消息列表
     * @return 模型回复
     */
    public String chat(List<Message> messages) {
        return chat(messages, null);
    }

    /**
     * 发送单条用户消息并获取回复
     *
     * @param content 用户消息内容
     * @return 模型回复
     */
    public String chat(String content) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", "You are a helpful assistant."));
        messages.add(new Message("user", content));
        return chat(messages);
    }

    /**
     * 消息实体类
     */
    @Data
    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
