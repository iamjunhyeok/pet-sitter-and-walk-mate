package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserInfoUpdateResponse {
    private String name;
    private String phoneNumber;
    private String zipCode;
    private String address1;
    private String address2;
}
