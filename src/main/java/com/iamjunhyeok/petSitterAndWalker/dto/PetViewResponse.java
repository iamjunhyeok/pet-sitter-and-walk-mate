package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Getter;

@Getter
public class PetViewResponse {
    private Long id;
    private String name;
    private String imageName;

    public PetViewResponse(Long id, String name, String imageName) {
        this.id = id;
        this.name = name;
        this.imageName = imageName;
    }
}
