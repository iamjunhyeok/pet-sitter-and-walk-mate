package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyInfoViewResponse {
    private String name;
    private String phoneNumber;
    private String zipCode;
    private String address1;
    private String address2;
}
