package com.pyeondeuk.db;

import com.pyeondeuk.model.ConvenienceStoreDTO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ConvenienceStoreMapper {

	/**
	 * 편의점 데이터를 데이터베이스에 삽입합니다.
	 * 
	 * @param store 삽입할 편의점 데이터
	 */
	void insertConvenienceStore(ConvenienceStoreDTO store);

	/**
	 * 중복된 편의점 데이터가 있는지 확인합니다.
	 * 
	 * @param csName          편의점 이름
	 * @param roadAddressName 도로명 주소
	 * @return 중복된 데이터의 개수
	 */
	int isDuplicateStore(@Param("csName") String csName);

	Double calculateAverageRating(@Param("csSeq") int csSeq);
	
	Double getAverageRating(@Param("csSeq") int csSeq);
	
	// 특정 편의점의 리뷰 가져오기
    List<String> getReviewsForStore(@Param("csSeq") int csSeq);

    // 별명과 태그 업데이트
    void updateNickAndTag(@Param("csSeq") int csSeq, 
                               @Param("csNick") String csNick, 
                               @Param("csTag") String csTag);
    
}

