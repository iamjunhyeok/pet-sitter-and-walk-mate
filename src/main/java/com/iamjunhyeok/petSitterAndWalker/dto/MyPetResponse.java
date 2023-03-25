package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
public class MyPetResponse {
    private Long id;
    private String name;
    private String breed;
    private int age;
    private String gender;
    private boolean isNeutered;
    private int weight;
    private String description;
    private PetPropertySimpleDto petType;
    private List<ImageSimpleDto> images;
}
