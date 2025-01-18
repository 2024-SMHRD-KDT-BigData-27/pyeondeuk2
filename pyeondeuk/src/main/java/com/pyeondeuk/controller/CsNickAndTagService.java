package com.pyeondeuk.controller;

import com.google.gson.JsonObject;
import com.pyeondeuk.db.ConvenienceStoreMapper;
import com.pyeondeuk.db.SqlSessionManager;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class CsNickAndTagService {

    /**
     * 편의점의 리뷰를 처리하여 별명과 태그를 생성하고 저장합니다.
     * @param csSeq 편의점 고유 식별자
     */
    public void processReviewsForConvenienceStore(int csSeq) {
    	try (SqlSession session = SqlSessionManager.getSqlSessionFactory().openSession()) {
    	    ConvenienceStoreMapper mapper = session.getMapper(ConvenienceStoreMapper.class);

    	    // 리뷰 데이터 가져오기
    	    System.out.println("SQL 쿼리 실행 중...");
    	    List<String> reviews = mapper.getReviewsForStore(csSeq);
    	    System.out.println("SQL 실행 완료, 리뷰 수: " + reviews.size());

    	    if (reviews.isEmpty()) {
    	        System.out.println("리뷰 데이터가 없습니다.");
    	        return;
    	    }

    	    String reviewText = String.join("\n", reviews);
    	    System.out.println("API 요청 데이터: " + reviewText);

    	    // OpenAI API 호출
    	    JsonObject result = OpenAICsNickTag.generateNickAndTag(reviews);

    	    // 결과에서 별명과 태그 추출
    	    String nickname = result.get("cs_nick").getAsString();
    	    String tags = result.get("cs_tag").getAsString();

    	    // DB 업데이트
    	    mapper.updateNickAndTag(csSeq, nickname, tags);
    	    session.commit();

    	    System.out.println("CS_SEQ: " + csSeq + ", 별명: " + nickname + ", 태그: " + tags);
    	} catch (Exception e) {
    	    throw new RuntimeException("리뷰 처리 중 오류 발생", e);
        }
    }

}
