package com.iamjunhyeok.petSitterAndWalker.petSitter.controller;

import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewListResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.service.PetSitterReviewService;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PetSitterReviewController {

    private final PetSitterReviewService petSitterReviewService;

    @PostMapping("/pet-sitters/{petSitterId}/requests/{requestId}/reviews")
    public ResponseEntity<ReviewRegisterResponse> registerReview(@RequestPart @Valid ReviewRegisterRequest request,
                                                                 @RequestPart List<MultipartFile> files,
                                                                 @PathVariable Long petSitterId,
                                                                 @PathVariable Long requestId,
                                                                 @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(petSitterReviewService.register(request, files, petSitterId, requestId, user), HttpStatus.CREATED);
    }

    @PutMapping("/pet-sitters/{petSitterId}/requests/{requestId}/reviews/{reviewId}")
    public ResponseEntity<ReviewUpdateResponse> updateReview(@RequestBody @Valid ReviewUpdateRequest request,
                                                             @RequestPart List<MultipartFile> files,
                                                             @PathVariable Long petSitterId,
                                                             @PathVariable Long requestId,
                                                             @PathVariable Long reviewId,
                                                             @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(petSitterReviewService.update(request, files, petSitterId, requestId, reviewId, user), HttpStatus.OK);
    }

    @DeleteMapping("/pet-sitters/{petSitterId}/requests/{requestId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long petSitterId,
                                             @PathVariable Long requestId,
                                             @PathVariable Long reviewId,
                                             @AuthenticationPrincipal User user) {
        petSitterReviewService.delete(petSitterId, requestId, reviewId, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/pet-sitters/{petSitterId}/reviews")
    public ResponseEntity<Page<ReviewListResponse>> reviewList(Pageable pageable, @PathVariable Long petSitterId) {
        return new ResponseEntity<>(petSitterReviewService.reviewList(pageable, petSitterId), HttpStatus.OK);
    }

    @GetMapping("/pet-sitters/{petSitterId}/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> review(@PathVariable Long petSitterId, @PathVariable Long reviewId) {
        return new ResponseEntity<>(petSitterReviewService.review(petSitterId, reviewId), HttpStatus.OK);
    }
}
