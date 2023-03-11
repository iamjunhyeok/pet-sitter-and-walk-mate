package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoUpdateResponse {
    private String name;
    private String phoneNumber;
    private String zipCode;
    private String address1;
    private String address2;
}
