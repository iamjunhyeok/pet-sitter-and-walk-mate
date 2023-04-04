package com.iamjunhyeok.petSitterAndWalker.pet.dto;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.Gender;
import com.iamjunhyeok.petSitterAndWalker.image.dto.ImageSimpleDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class MyPetResponse {
    private Long id;
    private String name;
    private String breed;
    private int age;
    private Gender gender;
    private boolean isNeutered;
    private int weight;
    private String description;
    private PetPropertySimpleDto petType;
    private List<ImageSimpleDto> images;
}
