package com.pyeondeuk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pyeondeuk.db.SqlSessionManager;

import lombok.Data;
import lombok.ToString;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class ConvenienceStoreDTO {
    @JsonProperty("place_name")
    private String csName;

    @JsonProperty("road_address_name")
    private String roadAddressName;

    @JsonProperty("address_name")
    private String addressName;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("x")
    private double longitude;

    @JsonProperty("y")
    private double latitude;
    
    private int csSeq;
    
    private String csTag;
    
    private String csNick;

    private int brandSeq; // 이마트24: 1, CU: 2, GS25: 3, 세븐일레븐: 4
    
    private double rating;
}