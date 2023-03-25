package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PetSitterListResponse {
    private String name;
    private String address;
    private float averageRating;
    private List<ImageSimpleDto> images;
}
