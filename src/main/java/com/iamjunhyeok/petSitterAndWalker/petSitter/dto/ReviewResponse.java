package com.iamjunhyeok.petSitterAndWalker.petSitter.dto;

import com.iamjunhyeok.petSitterAndWalker.image.dto.ImageSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.user.dto.UserSimpleDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ReviewResponse {
    private Long id;
    private int rating;
    private String comment;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    private UserSimpleDto user;

    private List<ImageSimpleDto> images;
}
