package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Getter;

@Getter
public class ImageDto {
    private Long id;
    private String name;

    public ImageDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
