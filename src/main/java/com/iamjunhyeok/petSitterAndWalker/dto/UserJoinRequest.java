package com.iamjunhyeok.petSitterAndWalker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserJoinRequest {

    @NotEmpty
    @Size(max = 20)
    private String name;

    @Email
    @NotEmpty
    @Size(max = 50)
    private String email;

    @NotEmpty
    @Size(max = 20)
    private String password;

    @NotEmpty
    @Size(max = 11)
    private String phoneNumber;

    @NotEmpty
    @Size(max = 10)
    private String zipCode;

    @NotEmpty
    @Size(max = 20)
    private String address1;

    @NotEmpty
    @Size(max = 20)
    private String address2;
}
