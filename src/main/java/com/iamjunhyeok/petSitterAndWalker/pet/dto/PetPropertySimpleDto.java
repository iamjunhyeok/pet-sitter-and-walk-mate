package com.iamjunhyeok.petSitterAndWalker.pet.dto;

import lombok.Getter;

@Getter
public class PetPropertySimpleDto {
    private Long id;
    private String name;
    private int order;

    public PetPropertySimpleDto(Long id, String name, int order) {
        this.id = id;
        this.name = name;
        this.order = order;
    }
}
