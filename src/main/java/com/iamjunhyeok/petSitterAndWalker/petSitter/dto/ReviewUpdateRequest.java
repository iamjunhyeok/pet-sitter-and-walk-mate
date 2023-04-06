package com.iamjunhyeok.petSitterAndWalker.petSitter.dto;

import lombok.Getter;

@Getter
public class ReviewUpdateRequest {
    private int rating;
    private String comment;

    public ReviewUpdateRequest(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }
}
