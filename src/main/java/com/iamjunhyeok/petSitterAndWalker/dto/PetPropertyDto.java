package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Getter;

@Getter
public class PetPropertyDto {
    private Long id;
    private String name;
    private int order;

    public PetPropertyDto(Long id, String name, int order) {
        this.id = id;
        this.name = name;
        this.order = order;
    }
}
