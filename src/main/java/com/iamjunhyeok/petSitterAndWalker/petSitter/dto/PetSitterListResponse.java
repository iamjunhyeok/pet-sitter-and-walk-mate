package com.iamjunhyeok.petSitterAndWalker.petSitter.dto;

import com.iamjunhyeok.petSitterAndWalker.image.dto.ImageSimpleDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PetSitterListResponse {
    private String name;
    private String address;
    private float averageRating;
    private List<ImageSimpleDto> images;

    @Builder
    public PetSitterListResponse(String name, String address, float averageRating, List<ImageSimpleDto> images) {
        this.name = name;
        this.address = address;
        this.averageRating = averageRating;
        this.images = images;
    }

    public PetSitterListResponse(String name, String address, float averageRating) {
        this.name = name;
        this.address = address;
        this.averageRating = averageRating;
    }
}
