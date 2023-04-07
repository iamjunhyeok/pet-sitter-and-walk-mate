package com.iamjunhyeok.petSitterAndWalker.image.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageSimpleDto {
    private Long id;
    private String name;

    public ImageSimpleDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
