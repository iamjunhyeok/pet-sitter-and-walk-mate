package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MyPetViewResponse {
    private Long id;
    private String name;
    private PetPropertyDto petType;
    private List<ImageDto> images;
}
