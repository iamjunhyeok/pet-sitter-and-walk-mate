package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Getter;

@Getter
public class PetSitterOptionSimpleDto {
    private Long id;
    private String name;
    private int price;

    public PetSitterOptionSimpleDto(Long id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
