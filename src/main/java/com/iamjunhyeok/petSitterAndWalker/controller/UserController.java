package com.iamjunhyeok.petSitterAndWalker.controller;

import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.MyInfoViewResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/my-info")
    public ResponseEntity<MyInfoViewResponse> viewMyInfo(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(userService.viewMyInfo(user), HttpStatus.OK);
    }

    @PutMapping("/my-info")
    public ResponseEntity<UserInfoUpdateResponse> updateMyInfo(@RequestBody @Valid UserInfoUpdateRequest request, @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(userService.updateMyInfo(request, user), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/password")
    public void changePassword(@RequestBody @Valid UserPasswordChangeRequest request, @AuthenticationPrincipal User user) {
        userService.changePassword(request, user);
    }

    @PostMapping("/users/{userId}/follow")
    public ResponseEntity<Void> follow(@PathVariable Long userId, @AuthenticationPrincipal User user) {
        userService.followOrUnfollow(userId, user, true);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{userId}/unfollow")
    public ResponseEntity<Void> unfollow(@PathVariable Long userId, @AuthenticationPrincipal User user) {
        userService.followOrUnfollow(userId, user, false);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
