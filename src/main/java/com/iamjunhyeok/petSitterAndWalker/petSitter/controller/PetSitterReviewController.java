package com.iamjunhyeok.petSitterAndWalker.petSitter.controller;

import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.service.PetSitterReviewService;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/pet-sitters/{petSitterId}/requests/{requestId}/reviews")
@RestController
public class PetSitterReviewController {

    private final PetSitterReviewService petSitterReviewService;

    @PostMapping
    public ResponseEntity<Void> registerReview(@RequestBody @Valid ReviewRegisterRequest request,
                                               @PathVariable Long petSitterId,
                                               @PathVariable Long requestId,
                                               @AuthenticationPrincipal User user) {
        petSitterReviewService.register(request, petSitterId, requestId, user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
