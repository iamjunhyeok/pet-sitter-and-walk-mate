package com.iamjunhyeok.petSitterAndWalker.petSitter.dto;

import com.iamjunhyeok.petSitterAndWalker.image.dto.ImageSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.pet.dto.PetPropertySimpleDto;
import com.iamjunhyeok.petSitterAndWalker.user.dto.UserSimpleDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PetSitterInfoResponse {
    private String name;
    private String address;

    private UserSimpleDto user;

    private ImageSimpleDto profileImage;

    private Long id;
    private String introduction;
    private int reviews;
    private float averageRating;

    private List<PetPropertySimpleDto> petTypes;
    private List<PetPropertySimpleDto> petSizes;
    private List<PetSitterOptionSimpleDto> options;
    private List<ImageSimpleDto> images;
}
