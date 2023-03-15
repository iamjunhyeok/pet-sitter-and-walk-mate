package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PetSitterRegisterResponse {
    private String introduction;
    private List<PetPropertyDto> petTypes;
    private List<PetPropertyDto> petSizes;
    private List<PetSitterOptionDto> options;
    private List<ImageDto> images;
}
