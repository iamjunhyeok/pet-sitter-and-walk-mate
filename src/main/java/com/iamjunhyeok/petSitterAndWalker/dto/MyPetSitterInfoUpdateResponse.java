package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MyPetSitterInfoUpdateResponse {
    private List<ImageSimpleDto> images;
    private String introduction;
    private boolean isAvailable;
    private List<PetPropertySimpleDto> petTypes;
    private List<PetPropertySimpleDto> petSizes;
    private List<PetSitterOptionSimpleDto> options;
}
