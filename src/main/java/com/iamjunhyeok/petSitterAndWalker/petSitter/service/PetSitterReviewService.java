package com.iamjunhyeok.petSitterAndWalker.petSitter.service;

import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterReview;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.repository.PetSitterRequestRepository;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PetSitterReviewService {

    private final PetSitterRequestRepository petSitterRequestRepository;

    @Transactional
    public void register(ReviewRegisterRequest request, Long petSitterId, Long requestId, User user) {
        PetSitterRequest petSitterRequest = petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Request ID 로 등록된 요청 정보가 존재하지 않음 : %s", requestId)));

        if (!petSitterRequest.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException(String.format("로그인된 사용자가 수행할 수 없는 요청 정보 : %s", requestId));
        }

        PetSitterReview petSitterReview = new PetSitterReview(request.getRating(), request.getComment());
        petSitterRequest.registerReview(petSitterReview);
    }
}
