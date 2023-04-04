package com.iamjunhyeok.petSitterAndWalker.user.dto;

import lombok.Getter;

@Getter
public class UserPasswordChangeResponse {
    private String password;

    public UserPasswordChangeResponse(String password) {
        this.password = password;
    }
}
