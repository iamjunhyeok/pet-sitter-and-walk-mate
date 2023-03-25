package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PetSitterRegisterResponse {
    private String introduction;
    private List<PetPropertySimpleDto> petTypes;
    private List<PetPropertySimpleDto> petSizes;
    private List<PetSitterOptionSimpleDto> options;
    private List<ImageSimpleDto> images;
}
