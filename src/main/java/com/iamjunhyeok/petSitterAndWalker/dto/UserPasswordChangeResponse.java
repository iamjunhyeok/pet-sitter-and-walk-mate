package com.iamjunhyeok.petSitterAndWalker.dto;

import lombok.Getter;

@Getter
public class UserPasswordChangeResponse {
    private String password;

    public UserPasswordChangeResponse(String password) {
        this.password = password;
    }
}
