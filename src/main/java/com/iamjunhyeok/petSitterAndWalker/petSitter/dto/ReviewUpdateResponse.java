package com.iamjunhyeok.petSitterAndWalker.petSitter.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewUpdateResponse {
    private Long id;
    private int rating;
    private String comment;
}
