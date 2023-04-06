package com.iamjunhyeok.petSitterAndWalker.petSitter.dto;

import lombok.Getter;

@Getter
public class ReviewRegisterResponse {
    private Long id;
    private int rating;
    private String comment;

    public ReviewRegisterResponse(Long id, int rating, String comment) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
    }
}
