package com.iamjunhyeok.petSitterAndWalker.image.dto;

import lombok.Getter;

@Getter
public class ImageSimpleDto {
    private Long id;
    private String name;

    public ImageSimpleDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
