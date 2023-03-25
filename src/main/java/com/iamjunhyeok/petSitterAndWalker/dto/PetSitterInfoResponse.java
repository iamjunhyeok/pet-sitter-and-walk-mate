package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PetSitterInfoResponse {
    private String name;
    private String address;
    private ImageSimpleDto profileImage;

    private String introduction;
    private int reviews;
    private float averageRating;

    private List<PetPropertySimpleDto> petTypes;
    private List<PetPropertySimpleDto> petSizes;
    private List<PetSitterOptionSimpleDto> options;
    private List<ImageSimpleDto> images;
}
