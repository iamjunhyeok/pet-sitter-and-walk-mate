package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PetSitterRegisterResponse {
    private String introduction;
    private List<String> petTypes;
    private List<String> petSizes;
    private List<PetSitterOptionResponse> options;
    private List<String> images;
}
