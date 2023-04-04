package com.iamjunhyeok.petSitterAndWalker.pet.dto;

import com.iamjunhyeok.petSitterAndWalker.image.dto.ImageSimpleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class MyPetListResponse {
    private Long id;
    private String name;
    private PetPropertySimpleDto petType;
    private List<ImageSimpleDto> images;
}
