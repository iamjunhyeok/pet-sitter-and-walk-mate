package com.iamjunhyeok.petSitterAndWalker.controller;

import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.UserInfoUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.UserInfoUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.UserJoinRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.UserJoinResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.UserPasswordChangeRequest;
import com.iamjunhyeok.petSitterAndWalker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<UserJoinResponse> join(@RequestBody @Valid UserJoinRequest request) {
        return new ResponseEntity<>(userService.join(request), HttpStatus.CREATED);
    }

    @PutMapping("/my-info")
    public ResponseEntity<UserInfoUpdateResponse> userInfoUpdate(@RequestBody @Valid UserInfoUpdateRequest request, @PathVariable Long userId) {
        return new ResponseEntity<>(userService.userInfoUpdate(request, userId), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/change-password")
    public void changePassword(@RequestBody @Valid UserPasswordChangeRequest request, @AuthenticationPrincipal User user) {
        userService.changePassword(request, user);
    }
}
