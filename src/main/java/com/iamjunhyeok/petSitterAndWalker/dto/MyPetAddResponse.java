package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyPetAddResponse {
    private Long id;
    private String name;
    private String breed;
    private int age;
    private String gender;
    private boolean isNeutered;
    private int weight;
    private String description;
    private List<ImageDto> images;
    private PetPropertyDto petType;
}
