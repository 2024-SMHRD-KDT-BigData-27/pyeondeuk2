package com.pyeondeuk.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class OpenAICsNickTag {

	private static final String API_URL = "https://api.openai.com/v1/chat/completions";
	private static final String API_KEY = "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";

	/**
	 * 리뷰 데이터를 기반으로 별명과 태그를 생성합니다.
	 * 
	 * @param reviews 리뷰 목록
	 * @return 생성된 별명과 태그를 포함한 JSON 객체
	 * @throws IOException API 호출 중 오류가 발생한 경우
	 */
	public static JsonObject generateNickAndTag(List<String> reviews) throws IOException {
	    OkHttpClient client = new OkHttpClient();

	    // 리뷰 데이터를 간결하게 만듦
	    reviews = reviews.subList(0, Math.min(reviews.size(), 10));
	    String reviewText = String.join("\n", reviews);

	    JsonObject requestBody = new JsonObject();
	    requestBody.addProperty("model", "gpt-3.5-turbo");

	    JsonArray messages = new JsonArray();
	    JsonObject systemMessage = new JsonObject();
	    systemMessage.addProperty("role", "system");
	    systemMessage.addProperty("content", "다음 리뷰를 기반으로 귀엽고 재미있는 편의점 별명(cs_nick)과 태그(cs_tag)를 생성하세요:");
	    messages.add(systemMessage);

	    JsonObject userMessage = new JsonObject();
	    userMessage.addProperty("role", "user");
	    userMessage.addProperty("content", reviewText);
	    messages.add(userMessage);

	    requestBody.add("messages", messages);
	    requestBody.addProperty("max_tokens", 5000);
	    requestBody.addProperty("temperature", 0.7);

	    RequestBody body = RequestBody.create(
	            MediaType.parse("application/json"),
	            requestBody.toString()
	    );

	    Request request = new Request.Builder()
	            .url("https://api.openai.com/v1/chat/completions")
	            .post(body)
	            .addHeader("Authorization", "Bearer " + API_KEY)
	            .addHeader("Content-Type", "application/json")
	            .build();

	    int retryCount = 0;
	    while (retryCount < 3) { // 재시도 횟수 증가
	        Response response = client.newCall(request).execute();
	        if (response.code() == 429) {
	            System.err.println("요청 한도 초과. 대기 후 재시도... (" + (retryCount + 1) + "/3)");
	            try {
	                Thread.sleep(3000); // 3초 대기
	            } catch (InterruptedException e) {
	                throw new RuntimeException("대기 중 인터럽트 발생", e);
	            }
	            retryCount++;
	        } else if (response.isSuccessful() && response.body() != null) {
	            String responseBody = response.body().string();
	            System.out.println("응답 본문 데이터: " + responseBody);

	            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
	            return jsonResponse;
	        } else {
	            System.err.println("응답 실패: " + response.message());
	            throw new IOException("OpenAI API 요청 실패: " + response.message());
	        }
	    }

	    throw new IOException("재시도 한도 초과로 요청 실패");
	}


}
