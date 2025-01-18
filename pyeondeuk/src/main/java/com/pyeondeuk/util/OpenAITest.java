package com.pyeondeuk.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;

public class OpenAITest {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "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"; // OpenAI API 키

    public static void main(String[] args) {
        try {
            String response = sendTestRequest();
            System.out.println("OpenAI 응답: " + response);
        } catch (IOException e) {
            System.err.println("API 요청 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String sendTestRequest() throws IOException {
        OkHttpClient client = new OkHttpClient();

        // 요청 데이터 준비
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-3.5-turbo");

        JsonArray messages = new JsonArray();

        // System 역할 메시지 추가
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", "You are a helpful assistant.");
        messages.add(systemMessage);

        // User 역할 메시지 추가
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", "Hello, how can I use OpenAI API?");
        messages.add(userMessage);

        // 메시지와 요청 설정
        requestBody.add("messages", messages);
        requestBody.addProperty("max_tokens", 50);
        requestBody.addProperty("temperature", 0.7);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                requestBody.toString()
        );

        // HTTP 요청 생성
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        // HTTP 요청 실행 및 응답 확인
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new IOException("HTTP 오류: " + response.code() + " - " + response.message());
            }
        }
    }
}