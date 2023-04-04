package com.iamjunhyeok.petSitterAndWalker.petSitter.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PetSitterOptionRequest {
    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    private int price;

    public PetSitterOptionRequest(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
