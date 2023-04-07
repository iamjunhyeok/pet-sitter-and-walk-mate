package com.iamjunhyeok.petSitterAndWalker.petSitter.dto;

import com.iamjunhyeok.petSitterAndWalker.image.dto.ImageSimpleDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReviewUpdateResponse {
    private Long id;
    private int rating;
    private String comment;

    private List<ImageSimpleDto> images;
}
