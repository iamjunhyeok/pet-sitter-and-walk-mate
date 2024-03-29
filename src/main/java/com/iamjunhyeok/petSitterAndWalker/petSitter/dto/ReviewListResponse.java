package com.iamjunhyeok.petSitterAndWalker.petSitter.dto;

import com.iamjunhyeok.petSitterAndWalker.image.dto.ImageSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.user.dto.UserSimpleDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReviewListResponse {
    private Long id;
    private int rating;
    private String comment;

    private UserSimpleDto user;

    private List<ImageSimpleDto> images;

    public ReviewListResponse(Long id, int rating, String comment) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
    }
}
