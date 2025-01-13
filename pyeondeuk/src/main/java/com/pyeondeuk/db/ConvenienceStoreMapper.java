package com.pyeondeuk.db;

import com.pyeondeuk.model.ConvenienceStoreDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ConvenienceStoreMapper {

    /**
     * 편의점 데이터를 데이터베이스에 삽입합니다.
     * @param store 삽입할 편의점 데이터
     */
    void insertConvenienceStore(ConvenienceStoreDTO store);

    /**
     * 중복된 편의점 데이터가 있는지 확인합니다.
     * @param csName 편의점 이름
     * @param roadAddressName 도로명 주소
     * @return 중복된 데이터의 개수
     */
    int isDuplicateStore(@Param("csName") String csName, @Param("roadAddressName") String roadAddressName);
}