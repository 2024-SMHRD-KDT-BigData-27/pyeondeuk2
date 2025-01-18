package com.pyeondeuk.controller;


import com.pyeondeuk.db.ConvenienceStoreMapper;
import com.pyeondeuk.db.SqlSessionManager;
import org.apache.ibatis.session.SqlSession;

public class RatingService {

    /**
     * 특정 편의점에 대한 평균 별점을 반환합니다.
     * @param csSeq 편의점의 고유 식별자
     * @return 평균 별점
     */
    public double getAverageRating(int csSeq) {
        try (SqlSession session = SqlSessionManager.getSqlSessionFactory().openSession()) {
            ConvenienceStoreMapper mapper = session.getMapper(ConvenienceStoreMapper.class);
            Double averageRating = mapper.getAverageRating(csSeq);
            System.out.println("CS_SEQ: " + csSeq + ", Average Rating: " + averageRating);
            return averageRating != null ? averageRating : 0.0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch average rating for CS_SEQ: " + csSeq, e);
        }
    }
}