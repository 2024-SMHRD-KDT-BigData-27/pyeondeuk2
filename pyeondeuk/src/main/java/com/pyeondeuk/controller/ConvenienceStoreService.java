package com.pyeondeuk.controller;

import com.pyeondeuk.db.ConvenienceStoreMapper;
import com.pyeondeuk.db.SqlSessionManager;
import com.pyeondeuk.model.ConvenienceStoreDTO;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.Map;

public class ConvenienceStoreService {

    private SqlSessionFactory sqlSessionFactory;

    // 생성자에서 SqlSessionFactory 초기화
    public ConvenienceStoreService() {
        this.sqlSessionFactory = SqlSessionManager.getSqlSessionFactory();
    }

    // 편의점 저장 메서드
    public static void saveStores(List<ConvenienceStoreDTO> stores) {
        try (SqlSession session = SqlSessionManager.getSqlSessionFactory().openSession(true)) {
            for (ConvenienceStoreDTO store : stores) {
                // Check for valid brand
                if (store.getBrandSeq() == 0) {
                    System.out.println("Skipping invalid brand: " + store.getCsName());
                    continue;
                }

                // Check if the store already exists
                int count = session.selectOne(
                        "com.pyeondeuk.db.ConvenienceStoreMapper.isDuplicateStore",
                        Map.of("csName", store.getCsName())
                );
                if (count > 0) {
                    System.out.println("Skipping duplicate store: " + store.getCsName());
                    continue;
                }

                // Save to database
                session.insert(
                        "com.pyeondeuk.db.ConvenienceStoreMapper.insertConvenienceStore",
                        store
                );
                System.out.println("Inserted: " + store.getCsName());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving to database: " + e.getMessage(), e);
        }
    }

 
}