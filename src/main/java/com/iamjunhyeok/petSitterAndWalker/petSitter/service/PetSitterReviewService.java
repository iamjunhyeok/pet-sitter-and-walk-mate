package com.iamjunhyeok.petSitterAndWalker.petSitter.service;

import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterReview;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.repository.PetSitterRequestRepository;
import com.iamjunhyeok.petSitterAndWalker.petSitter.repository.PetSitterReviewRepository;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PetSitterReviewService {

    private final PetSitterRequestRepository petSitterRequestRepository;

    private final PetSitterReviewRepository petSitterReviewRepository;

    @Transactional
    public ReviewRegisterResponse register(ReviewRegisterRequest request, Long petSitterId, Long requestId, User user) {
        log.info("펫 시터 리뷰 등록 시도 : {}", requestId);
        PetSitterRequest petSitterRequest = getPetSitterRequest(petSitterId, requestId, user);

        PetSitterReview petSitterReview = new PetSitterReview(request.getRating(), request.getComment());
        petSitterRequest.registerReview(petSitterReview);

        log.info("펫 시터 리뷰 등록 성공 : {}", requestId);
        return new ReviewRegisterResponse(petSitterReview.getId(), petSitterReview.getRating(), petSitterReview.getComment());
    }

    @NotNull
    private PetSitterRequest getPetSitterRequest(Long petSitterId, Long requestId, User user) {
        PetSitterRequest petSitterRequest = petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Request ID 로 등록된 요청 정보가 존재하지 않음 : %s", requestId)));

        if (!petSitterRequest.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException(String.format("로그인된 사용자가 수행할 수 없는 요청 정보 : %s", requestId));
        }
        return petSitterRequest;
    }

    @Transactional
    public ReviewUpdateResponse update(ReviewUpdateRequest request, Long petSitterId, Long requestId, Long reviewId, User user) {
        log.info("펫 시터 리뷰 수정 시도 : {}", reviewId);
        PetSitterRequest petSitterRequest = getPetSitterRequest(petSitterId, requestId, user);

        PetSitterReview review = petSitterRequest.getReview();
        if (review == null) {
            throw new EntityNotFoundException(String.format("리뷰가 존재하지 않음", reviewId));
        }

        review.update(request.getRating(), request.getComment());

        ReviewUpdateResponse response = new ReviewUpdateResponse();
        BeanUtils.copyProperties(review, response);

        log.info("펫 시터 리뷰 수정 성공 : {}", reviewId);
        return response;
    }

    @Transactional
    public void delete(Long petSitterId, Long requestId, Long reviewId, User user) {
        log.info("펫 시터 리뷰 삭제 시도 : {}", reviewId);
        PetSitterRequest petSitterRequest = getPetSitterRequest(petSitterId, requestId, user);

        PetSitterReview review = petSitterRequest.getReview();
        if (review == null) {
            throw new EntityNotFoundException(String.format("리뷰가 존재하지 않음", reviewId));
        }
        review.delete();
        log.info("펫 시터 리뷰 삭제 성공 : {}", reviewId);
    }
}
