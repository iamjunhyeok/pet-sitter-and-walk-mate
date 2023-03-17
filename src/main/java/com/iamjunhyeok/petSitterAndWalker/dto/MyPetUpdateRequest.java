package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Getter
public class MyPetUpdateRequest extends MyPetRequest {

    private List<Long> deleteImageIds = new ArrayList<>();
}
