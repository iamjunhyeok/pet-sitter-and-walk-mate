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

    @NotEmpty(message = "이름은 비어 있을 수 없습니다")
    @Size(max = 20)
    private String name;

    @Email(regexp = "^[\\w!#$%&'*+/=?^_`{|}~-]+(\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,})$\n")
    @NotEmpty
    @Size(max = 50)
    private String email;

    @NotEmpty(message = "비밀번호는 비어 있을 수 없습니다")
    @Size(max = 20)
    private String password;

    @NotEmpty(message = "핸드폰번호는 비어 있을 수 없습니다")
    @Size(max = 11)
    private String phoneNumber;

    @NotEmpty(message = "우편번호는 비어 있을 수 없습니다")
    @Size(max = 10)
    private String zipCode;

    @NotEmpty(message = "주소는 비어 있을 수 없습니다")
    @Size(max = 20)
    private String address1;

    @NotEmpty(message = "상세 주소는 비어 있을 수 없습니다")
    @Size(max = 20)
    private String address2;
}
