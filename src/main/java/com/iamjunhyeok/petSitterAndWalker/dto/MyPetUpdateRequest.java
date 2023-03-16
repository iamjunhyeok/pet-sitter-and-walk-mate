package com.iamjunhyeok.petSitterAndWalker.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MyPetUpdateRequest {
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

    private List<Long> deleteImageIds = new ArrayList<>();

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }
}
