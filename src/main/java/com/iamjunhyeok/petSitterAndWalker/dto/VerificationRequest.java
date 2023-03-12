package com.iamjunhyeok.petSitterAndWalker.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class VerificationRequest {

    @NotEmpty
    @Size(min = 11, max = 11)
    private String phoneNumber;
}
