package com.iamjunhyeok.petSitterAndWalker.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@Getter
public class PetSitterRegisterRequest {

    @NotEmpty
    private String intro;

    @Min(0)
    @Max(100)
    private int experience;

    @NotEmpty
    private List<Long> petTypeId;

    @NotEmpty
    private List<Long> petSizeId;

    private List<PetSitterOptionRequest> options;

    private List<MultipartFile> images;

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }
}
