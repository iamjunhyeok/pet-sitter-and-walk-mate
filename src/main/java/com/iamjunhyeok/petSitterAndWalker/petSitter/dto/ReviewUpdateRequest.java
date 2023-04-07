package com.iamjunhyeok.petSitterAndWalker.petSitter.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewUpdateRequest {
    private int rating;
    private String comment;

    private List<Long> deleteImageIds;

    public ReviewUpdateRequest(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public ReviewUpdateRequest(int rating, String comment, List<Long> deleteImageIds) {
        this.rating = rating;
        this.comment = comment;
        this.deleteImageIds = deleteImageIds;
    }
}
