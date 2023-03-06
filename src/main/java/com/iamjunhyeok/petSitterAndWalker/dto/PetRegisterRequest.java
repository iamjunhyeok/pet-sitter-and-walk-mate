package com.iamjunhyeok.petSitterAndWalker.dto;

import com.iamjunhyeok.petSitterAndWalker.constants.Gender;
import com.iamjunhyeok.petSitterAndWalker.validator.ValueOfEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
public class PetRegisterRequest {
    @NotEmpty
    private String name;

    @NotEmpty
    private String breed;

    @Min(0)
    @Max(100)
    private int age;

    @ValueOfEnum(enumClass = Gender.class)
    @NotEmpty
    private String gender;

    private boolean isNeutered;

    @Min(0)
    @Max(100)
    private int weight;

    private String intro;

    private List<MultipartFile> images;
}
