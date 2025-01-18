package com.pyeondeuk.util;

import java.util.Arrays;
import com.google.gson.JsonObject;
import com.pyeondeuk.controller.OpenAIRequestDebugger;

public class TestOpenAIRequest {

    public static void main(String[] args) {
        try {
            // 테스트용 리뷰 데이터
            String[] reviews = {
                "직원이 친절하고 매장이 깨끗했어요.",
                "신상품이 다양해서 쇼핑하기 좋았습니다.",
                "라면 코너가 잘 정리되어 있어 편리했어요."
            };

            // OpenAI API 요청 및 결과 출력
            JsonObject result = OpenAIRequestDebugger.generateNickAndTag(Arrays.asList(reviews));
            System.out.println("생성된 결과: " + result);
        } catch (Exception e) {
            System.err.println("오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}