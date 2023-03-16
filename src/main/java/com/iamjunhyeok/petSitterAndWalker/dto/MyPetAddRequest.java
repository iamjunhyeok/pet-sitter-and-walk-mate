package com.iamjunhyeok.petSitterAndWalker.dto;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.Gender;
import com.iamjunhyeok.petSitterAndWalker.validator.ValueOfEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Builder
public class MyPetAddRequest {
    @NotEmpty
    private String name;

    @NotEmpty
    private String breed;

    @Min(0)
    @Max(100)
    private int age;

    @ValueOfEnum(enumClass = Gender.class)
    private String gender;

    private boolean isNeutered;

    @Min(0)
    @Max(100)
    private int weight;

    private String description;

    private List<MultipartFile> images;

    private Long petTypeId;

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }
}
