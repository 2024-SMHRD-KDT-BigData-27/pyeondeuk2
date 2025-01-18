package com.pyeondeuk.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class OpenAIRequestDebugger {

    private static final String API_URL = "https://api.openai.com/v1/completions";
    private static final String API_KEY = "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";

    /**
     * 리뷰 데이터를 기반으로 귀엽고 재미있는 별명(cs_nick)과 태그(cs_tag)를 생성합니다.
     * 요청 데이터와 응답 데이터를 디버깅합니다.
     *
     * @param reviews 리뷰 목록
     * @return 생성된 별명과 태그를 포함한 JSON 객체
     * @throws IOException API 호출 중 오류 발생 시 예외 처리
     */
    public static JsonObject generateNickAndTag(List<String> reviews) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // 리뷰 데이터를 하나의 텍스트로 합칩니다.
        String reviewText = String.join("\n", reviews);

        // 요청 데이터 제한 체크
        if (reviewText.length() > 5000) {
            throw new IllegalArgumentException("리뷰 데이터가 너무 깁니다.");
        }

        // OpenAI API 요청 본문 생성
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "text-davinci-003");
        requestBody.addProperty("prompt", "다음 리뷰를 기반으로 귀엽고 재미있는 편의점 별명(cs_nick)과 태그(cs_tag)를 생성하세요:\n" + reviewText);
        requestBody.addProperty("max_tokens", 100);
        requestBody.addProperty("temperature", 0.7);

        // 요청 데이터 디버깅 출력
        System.out.println("API 요청 URL: " + API_URL);
        System.out.println("API 요청 본문: " + requestBody);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                requestBody.toString()
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        // API 요청 보내기
        Response response = client.newCall(request).execute();

        // 응답 상태 코드 확인
        System.out.println("응답 상태 코드: " + response.code());

        // 응답이 실패한 경우 예외 처리
        if (!response.isSuccessful() || response.body() == null) {
            System.err.println("OpenAI API 응답 실패 메시지: " + response.message());
            throw new IOException("OpenAI API 요청 실패: " + response.message());
        }

        // 응답 본문 디버깅 출력
        String responseBody = response.body().string();
        System.out.println("응답 본문 데이터: " + responseBody);

        // 응답 데이터 파싱
        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
        String resultText = jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject().get("text").getAsString();

        // 결과 데이터를 JSON으로 파싱하여 반환
        return JsonParser.parseString(resultText).getAsJsonObject();
    }
}
