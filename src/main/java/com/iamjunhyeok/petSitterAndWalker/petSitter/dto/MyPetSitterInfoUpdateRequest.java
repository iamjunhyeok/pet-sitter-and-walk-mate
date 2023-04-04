package com.iamjunhyeok.petSitterAndWalker.petSitter.dto;

import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterOptionRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@Getter
public class MyPetSitterInfoUpdateRequest {
    @NotEmpty
    private String introduction;

    @NotEmpty
    private List<Long> petTypeIds;

    @NotEmpty
    private List<Long> petSizeIds;

    private List<PetSitterOptionRequest> options;

    private List<MultipartFile> images;

    private List<Long> deleteImageIds;

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }
}
