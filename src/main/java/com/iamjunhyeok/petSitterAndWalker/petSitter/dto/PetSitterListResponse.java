package com.iamjunhyeok.petSitterAndWalker.petSitter.dto;

import com.iamjunhyeok.petSitterAndWalker.image.dto.ImageSimpleDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PetSitterListResponse {
    private String name;
    private String address;
    private float averageRating;
    private List<ImageSimpleDto> images;
}
