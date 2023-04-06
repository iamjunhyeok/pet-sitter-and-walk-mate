package com.iamjunhyeok.petSitterAndWalker.petSitter.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReviewRegisterRequest {

    @Min(1)
    @Max(5)
    private int rating;

    @NotEmpty
    private String comment;

    public ReviewRegisterRequest(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }
}
