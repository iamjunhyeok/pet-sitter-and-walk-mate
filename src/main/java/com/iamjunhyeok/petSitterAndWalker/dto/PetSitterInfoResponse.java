package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PetSitterInfoResponse {
    private String name;
    private String address;
    private ImageDto profileImage;

    private String introduction;
    private int reviews;
    private float averageRating;

    private List<PetPropertyDto> petTypes;
    private List<PetPropertyDto> petSizes;
    private List<PetSitterOptionDto> options;
    private List<ImageDto> images;
}
