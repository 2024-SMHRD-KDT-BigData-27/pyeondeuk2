package com.pyeondeuk.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pyeondeuk.controller.ConvenienceStoreService;
import com.pyeondeuk.model.ConvenienceStoreDTO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CsJsonToDatabase {

    public static void main(String[] args) {
        try {
            // Step 1: Parse JSON
            List<ConvenienceStoreDTO> stores = parseJson("C:\\Users\\smhrd\\Desktop\\Hacksim\\pyeondeuk\\convenience_stores.json");

            // Step 2: Filter and Save
            filterAndSaveStores(stores);

            System.out.println("Data processing complete!");
        } catch (Exception e) {
            System.err.println("Error processing JSON file or saving to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<ConvenienceStoreDTO> parseJson(String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<ConvenienceStoreDTO> stores = mapper.readValue(new File(filePath), new TypeReference<List<ConvenienceStoreDTO>>() {});

        for (ConvenienceStoreDTO store : stores) {
            // Set brandSeq based on category name
            if (store.getCategoryName().endsWith("GS25")) {
                store.setBrandSeq(3);
            } else if (store.getCategoryName().endsWith("CU")) {
                store.setBrandSeq(2);
            } else if (store.getCategoryName().endsWith("세븐일레븐")) {
                store.setBrandSeq(4);
            } else if (store.getCategoryName().endsWith("이마트24")) {
                store.setBrandSeq(1);
            } else {
                store.setBrandSeq(0);
            }
        }

        return stores;
    }

    private static void filterAndSaveStores(List<ConvenienceStoreDTO> stores) {
        List<ConvenienceStoreDTO> validStores = new ArrayList<>();
        for (ConvenienceStoreDTO store : stores) {
            if (store.getBrandSeq() != 0) {
                validStores.add(store);
            }
        }

        ConvenienceStoreService.saveStores(validStores);
    }
}