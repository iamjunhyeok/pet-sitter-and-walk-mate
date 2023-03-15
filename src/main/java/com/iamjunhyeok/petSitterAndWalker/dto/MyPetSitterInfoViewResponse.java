package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MyPetSitterInfoViewResponse {
    private List<ImageDto> images;
    private String introduction;
    private boolean isAvailable;
    private List<PetPropertyDto> petTypes;
    private List<PetPropertyDto> petSizes;
    private List<PetSitterOptionDto> options;
}
