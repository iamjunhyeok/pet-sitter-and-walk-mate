package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Getter;

@Getter
public class PetSitterOptionDto {
    private Long id;
    private String name;
    private int price;

    public PetSitterOptionDto(Long id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
