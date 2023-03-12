package com.iamjunhyeok.petSitterAndWalker.dto;

import com.iamjunhyeok.petSitterAndWalker.exception.PasswordMismatchException;
import lombok.Getter;

@Getter
public class UserPasswordChangeRequest {
    private String oldPassword;
    private String newPassword;
    private String retypeNewPassword;

    public UserPasswordChangeRequest(String oldPassword, String newPassword, String retypeNewPassword) {
        this.oldPassword = oldPassword;
        if (!newPassword.equals(retypeNewPassword)) {
            throw new PasswordMismatchException();
        }
        this.newPassword = newPassword;
        this.retypeNewPassword = retypeNewPassword;
    }
}
