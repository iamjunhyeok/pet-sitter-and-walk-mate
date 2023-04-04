package com.iamjunhyeok.petSitterAndWalker.pet.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@SuperBuilder
@Getter
public class MyPetRequest {

    @NotEmpty
    private String name;

    @NotEmpty
    private String breed;

    @Min(0)
    @Max(100)
    private int age;

    private boolean isNeutered;

    @Min(0)
    @Max(100)
    private int weight;

    private String description;

    private List<MultipartFile> images;

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }
}
