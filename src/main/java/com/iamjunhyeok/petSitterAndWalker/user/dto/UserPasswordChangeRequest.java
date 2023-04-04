package com.iamjunhyeok.petSitterAndWalker.user.dto;

import com.iamjunhyeok.petSitterAndWalker.exception.PasswordMismatchException;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class UserPasswordChangeRequest {

    @NotEmpty
    private String oldPassword;

    @NotEmpty
    private String newPassword;

    @NotEmpty
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
