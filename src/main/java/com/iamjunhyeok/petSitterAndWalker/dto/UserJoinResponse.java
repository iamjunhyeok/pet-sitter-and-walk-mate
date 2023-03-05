package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserJoinResponse {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String zipCode;
    private String address1;
    private String address2;

}
