package com.pyeondeuk.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetConvenienceStore {

    private static final String[] API_KEYS = {
        "291d8853ce37026f892b5ea434bd026c",
        "910a9ee8cfc3c71a7f64b87592e259dc",
        "1214d5c7c40cadf0a7737db14ec85bde",
        "4c4013a5dae59124fbb4704555cb86bb"
    };
    private static final String BASE_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";
    private static final String KEYWORD = URLEncoder.encode("편의점", StandardCharsets.UTF_8);
    private static final double LAT_START = 34.0;
    private static final double LAT_END = 35.5;
    private static final double LNG_START = 126.0; // 여기가 정적 상수 선언입니다.
    private static final double LNG_END = 127.5;
    private static final double STEP = 0.005;
    private static final int RADIUS = 700;

    public static void main(String[] args) {
        List<JSONObject> allResults = Collections.synchronizedList(new ArrayList<>());
        Set<String> uniquePlaces = Collections.synchronizedSet(new HashSet<>());

        double[][] latRanges = {
            {34.0, 34.375},
            {34.375, 34.75},
            {34.75, 35.125},
            {35.125, 35.5}
        };

        ExecutorService executor = Executors.newFixedThreadPool(API_KEYS.length);

        try {
            for (int i = 0; i < API_KEYS.length; i++) {
                final String apiKey = API_KEYS[i];
                final double latStart = latRanges[i][0];
                final double latEnd = latRanges[i][1];

                executor.submit(() -> {
                    try {
                        for (double lat = latStart; lat <= latEnd; lat += STEP) {
                            for (double lng = LNG_START; lng <= LNG_END; lng += STEP) { // LNG_START로 수정
                                for (int page = 1; page <= 3; page++) {
                                    System.out.printf("현재 검색 중: 위도=%.3f, 경도=%.3f, 페이지=%d, API_KEY=%s%n", lat, lng, page, apiKey);

                                    JSONObject response = fetchData(lat, lng, page, apiKey);
                                    if (response != null) {
                                        JSONArray documents = response.getJSONArray("documents");
                                        for (int j = 0; j < documents.length(); j++) {
                                            JSONObject place = documents.getJSONObject(j);
                                            String id = place.getString("id");

                                            if (uniquePlaces.add(id)) {
                                                allResults.add(place);
                                            }
                                        }

                                        if (documents.length() < 15) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.printf("오류 발생: 위도=%.3f, 경도=%.3f, API_KEY=%s, 오류 메시지: %s%n", latStart, LNG_START, apiKey, e.getMessage()); // LNG_START로 수정
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);

            saveDataToFile(allResults, "convenience_stores.json");
            System.out.println("총 데이터 개수: " + allResults.size());
            System.out.println("데이터가 convenience_stores.json 파일에 저장되었습니다.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static JSONObject fetchData(double lat, double lng, int page, String apiKey) {
        try {
            String query = String.format("?query=%s&x=%f&y=%f&radius=%d&page=%d", KEYWORD, lng, lat, RADIUS, page);
            String urlString = BASE_URL + query;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "KakaoAK " + apiKey);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return new JSONObject(response.toString());
            } else {
                System.err.printf("HTTP 오류: 코드=%d, 위도=%.3f, 경도=%.3f, API_KEY=%s%n", responseCode, lat, lng, apiKey);
            }
        } catch (Exception e) {
            System.err.printf("데이터 가져오기 오류: 위도=%.3f, 경도=%.3f, 페이지=%d, API_KEY=%s, 오류 메시지: %s%n", lat, lng, page, apiKey, e.getMessage());
        }
        return null;
    }

    private static void saveDataToFile(List<JSONObject> data, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            JSONArray jsonArray = new JSONArray(data);
            writer.write(jsonArray.toString(4));
            System.out.println("파일 저장 완료: " + fileName);
        } catch (IOException e) {
            System.err.println("파일 저장 중 오류 발생: " + e.getMessage());
        }
    }
}