package com.iamjunhyeok.petSitterAndWalker.petSitter.controller;

import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.service.PetSitterReviewService;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/pet-sitters/{petSitterId}/requests/{requestId}/reviews")
@RestController
public class PetSitterReviewController {

    private final PetSitterReviewService petSitterReviewService;

    @PostMapping
    public ResponseEntity<ReviewRegisterResponse> registerReview(@RequestBody @Valid ReviewRegisterRequest request,
                                                                 @PathVariable Long petSitterId,
                                                                 @PathVariable Long requestId,
                                                                 @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(petSitterReviewService.register(request, petSitterId, requestId, user), HttpStatus.CREATED);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewUpdateResponse> updateReview(@RequestBody @Valid ReviewUpdateRequest request,
                                                             @PathVariable Long petSitterId,
                                                             @PathVariable Long requestId,
                                                             @PathVariable Long reviewId,
                                                             @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(petSitterReviewService.update(request, petSitterId, requestId, reviewId, user), HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long petSitterId,
                                             @PathVariable Long requestId,
                                             @PathVariable Long reviewId,
                                             @AuthenticationPrincipal User user) {
        petSitterReviewService.delete(petSitterId, requestId, reviewId, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
