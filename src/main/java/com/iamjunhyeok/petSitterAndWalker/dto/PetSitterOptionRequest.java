package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PetSitterOptionRequest {
    private String name;
    private String description;
    private int price;

    public PetSitterOptionRequest(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
