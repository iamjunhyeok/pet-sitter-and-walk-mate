package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Getter;

@Getter
public class PetSimpleDto {
    private Long id;
    private String name;
    private ImageSimpleDto image;

    public PetSimpleDto(Long id, String name, ImageSimpleDto image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }
}
