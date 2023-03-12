package com.iamjunhyeok.petSitterAndWalker.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class VerifyRequest {

    @NotEmpty
    @Size(min = 11, max = 11)
    private String phoneNumber;

    @NotEmpty
    @Size(min = 6, max = 6)
    private String verificationCode;
}
