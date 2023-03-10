package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Getter;

@Getter
public class PetSitterOptionResponse {
    private String name;
    private String description;
    private int price;

    public PetSitterOptionResponse(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
