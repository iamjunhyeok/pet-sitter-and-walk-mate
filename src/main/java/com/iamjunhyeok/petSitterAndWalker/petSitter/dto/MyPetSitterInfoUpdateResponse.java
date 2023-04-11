package com.iamjunhyeok.petSitterAndWalker.petSitter.dto;

import com.iamjunhyeok.petSitterAndWalker.image.dto.ImageSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.pet.dto.PetPropertySimpleDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MyPetSitterInfoUpdateResponse {
    private String introduction;
    private boolean isAvailable;
    private List<PetPropertySimpleDto> petTypes;
    private List<PetPropertySimpleDto> petSizes;
    private List<PetSitterOptionSimpleDto> options;
    private List<ImageSimpleDto> images;
}
