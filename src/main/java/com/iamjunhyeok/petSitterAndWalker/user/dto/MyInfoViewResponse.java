package com.iamjunhyeok.petSitterAndWalker.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class MyInfoViewResponse {
    private String name;
    private String phoneNumber;
    private String zipCode;
    private String address1;
    private String address2;
}
