package com.iamjunhyeok.petSitterAndWalker.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInfoUpdateRequest {

    @NotEmpty
    private String name;

    @NotEmpty
    private String phoneNumber;

    @NotEmpty
    private String zipCode;

    @NotEmpty
    private String address1;

    @NotEmpty
    private String address2;
}
