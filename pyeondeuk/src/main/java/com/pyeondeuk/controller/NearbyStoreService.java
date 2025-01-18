package com.pyeondeuk.controller;

import com.pyeondeuk.db.SqlSessionManager;
import com.pyeondeuk.model.ConvenienceStoreDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class NearbyStoreService {
	/**
	 * 반경 내의 편의점 데이터를 가져옵니다.
	 * 
	 * @param latitude  중심 좌표의 위도
	 * @param longitude 중심 좌표의 경도
	 * @param radius    검색 반경 (미터 단위)
	 * @return 반경 내 편의점 리스트
	 */
	public List<ConvenienceStoreDTO> getNearbyStores(double latitude, double longitude, double radius) {
	    GeoQuery query = new GeoQuery(latitude, longitude, radius);

	    System.out.println("GeoQuery 값: " + query);

	    try (SqlSession session = SqlSessionManager.getSqlSessionFactory().openSession()) {
	        List<ConvenienceStoreDTO> stores = session.selectList("com.pyeondeuk.db.ConvenienceStoreMapper.getNearbyStores", query);

	        RatingService ratingService = new RatingService();
	        for (ConvenienceStoreDTO store : stores) {
	            double rating = ratingService.getAverageRating(store.getCsSeq());
	            System.out.println("Rating for " + store.getCsSeq() + ": " + rating);
	            store.setRating(rating); // 별점 설정
	        }

	        System.out.println("조회된 편의점 데이터:");
	        for (ConvenienceStoreDTO store : stores) {
	            System.out.println(store);
	        }

	        return stores;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to fetch nearby stores", e);
	    }
	}
	/**
	 * GeoQuery 클래스는 MyBatis 쿼리에 전달할 좌표 및 반경 정보를 캡슐화합니다.
	 */
	@Data
	@AllArgsConstructor
	public static class GeoQuery {
		private double latitude;
		private double longitude;
		private double radius;

	}
}

