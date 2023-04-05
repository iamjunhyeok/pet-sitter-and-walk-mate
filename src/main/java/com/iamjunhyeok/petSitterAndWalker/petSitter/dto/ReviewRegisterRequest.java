package com.iamjunhyeok.petSitterAndWalker.petSitter.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class ReviewRegisterRequest {

    @Min(1)
    @Max(5)
    private int rating;

    @NotEmpty
    private String comment;
}
