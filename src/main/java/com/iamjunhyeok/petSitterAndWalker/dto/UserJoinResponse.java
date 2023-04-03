package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
