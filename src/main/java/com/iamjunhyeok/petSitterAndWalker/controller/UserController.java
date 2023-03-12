package com.iamjunhyeok.petSitterAndWalker.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserJoinResponse> join(@RequestBody @Valid UserJoinRequest request) {
        return new ResponseEntity<>(userService.join(request), HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserInfoUpdateResponse> userInfoUpdate(@RequestBody @Valid UserInfoUpdateRequest request, @PathVariable Long userId) {
        return new ResponseEntity<>(userService.userInfoUpdate(request, userId), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{userId}/change-password")
    public void changePassword(@RequestBody @Valid UserPasswordChangeRequest request, @PathVariable Long userId) {
        userService.changePassword(userId, request);
    }
}
