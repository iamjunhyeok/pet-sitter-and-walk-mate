package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MyPetUpdateResponse {
    private Long id;
    private String name;
    private String breed;
    private int age;
    private String gender;
    private boolean isNeutered;
    private int weight;
    private String description;
    private PetPropertyDto petType;
    private List<ImageDto> images;
}
