package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PetRegisterResponse {
    private Long id;
    private String name;
    private String breed;
    private int age;
    private String gender;
    private boolean isNeutered;
    private int weight;
    private String intro;
    private List<String> images;
}
