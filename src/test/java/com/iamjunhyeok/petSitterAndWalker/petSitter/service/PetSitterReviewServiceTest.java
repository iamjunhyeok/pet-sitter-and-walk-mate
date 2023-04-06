package com.iamjunhyeok.petSitterAndWalker.petSitter.service;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.RequestStatus;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetSitterReviewServiceTest {

    @InjectMocks
    private PetSitterReviewService petSitterReviewService;

    @Mock
    private PetSitterRequestRepository petSitterRequestRepository;

    @Mock
    private PetSitterReviewRepository petSitterReviewRepository;

    private Long petSitterId = 1L;
    private Long requestId = 1L;
    private User requester;
    private User otherUser;

    @BeforeEach
    void setup() {
        requester = User.builder()
                .id(1L)
                .build();
        otherUser = User.builder()
                .id(2L)
                .build();
    }

    @Test
    @DisplayName("리뷰 등록 실패 - 펫 시터에 대한 요청이 아닌 경우")
    void testWhenNotRequestForPetSitter() {
        // Arrange
        when(petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> petSitterReviewService.register(new ReviewRegisterRequest(), petSitterId, requestId, new User()));
    }

    @Test
    @DisplayName("리뷰 등록 실패 - 로그인된 사용자가 수행 권한이 없음")
    void testWhenReviewCannotBeRegistered() {
        // Arrange
        ReviewRegisterRequest request = new ReviewRegisterRequest(3, "Soso~");
        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .user(requester)
                .build();
        when(petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId))
                .thenReturn(Optional.ofNullable(petSitterRequest));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> petSitterReviewService.register(request, petSitterId, requestId, otherUser));
    }

    @Test
    @DisplayName("리뷰 등록 실패 - 상태가 수락 상태가 아닐 때")
    void test1() {
        // Arrange
        ReviewRegisterRequest request = new ReviewRegisterRequest(3, "Soso~");
        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .user(requester)
                .endDate(LocalDateTime.MIN)
                .status(RequestStatus.REQUESTED)
                .build();
        when(petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId))
                .thenReturn(Optional.ofNullable(petSitterRequest));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> petSitterReviewService.register(request, petSitterId, requestId, requester));
    }

    @Test
    @DisplayName("리뷰 등록 실패 - 종료 시간이 아직 지나지 않음")
    void test2() {
        // Arrange
        ReviewRegisterRequest request = new ReviewRegisterRequest(3, "Soso~");
        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .user(requester)
                .endDate(LocalDateTime.MAX)
                .status(RequestStatus.ACCEPTED)
                .build();
        when(petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId))
                .thenReturn(Optional.ofNullable(petSitterRequest));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> petSitterReviewService.register(request, petSitterId, requestId, requester));
    }

    @Test
    @DisplayName("리뷰 등록 성공")
    void testWhenValidInput() {
        // Arrange
        ReviewRegisterRequest request = new ReviewRegisterRequest(3, "Soso~");
        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .user(requester)
                .endDate(LocalDateTime.MIN)
                .status(RequestStatus.ACCEPTED)
                .build();
        when(petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId))
                .thenReturn(Optional.ofNullable(petSitterRequest));

        // Act
        ReviewRegisterResponse response = petSitterReviewService.register(request, petSitterId, requestId, requester);

        // Assert
        assertNotNull(response);
        assertEquals(request.getRating(), response.getRating());
        assertEquals(request.getComment(), response.getComment());
    }

    @Test
    @DisplayName("리뷰가 존재하지 않음")
    void testWhenNotExistsReview() {
        // Arrange
        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .user(requester)
                .endDate(LocalDateTime.MIN)
                .status(RequestStatus.ACCEPTED)
                .review(null)
                .build();
        when(petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId))
                .thenReturn(Optional.ofNullable(petSitterRequest));

        ReviewUpdateRequest request = new ReviewUpdateRequest(4, "Great!!");

        Long reviewId = 1L;

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> petSitterReviewService.update(request, petSitterId, requestId, reviewId, requester));
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void testWhenValidInputUpdate() {
        // Arrange
        PetSitterReview review = new PetSitterReview(3, "SoSo~");

        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .user(requester)
                .endDate(LocalDateTime.MIN)
                .status(RequestStatus.ACCEPTED)
                .review(review)
                .build();
        when(petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId))
                .thenReturn(Optional.ofNullable(petSitterRequest));

        ReviewUpdateRequest request = new ReviewUpdateRequest(4, "Great!!");

        Long reviewId = 1L;

        // Act
        ReviewUpdateResponse response = petSitterReviewService.update(request, petSitterId, requestId, reviewId, requester);

        // Assert
        assertEquals(request.getRating(), response.getRating());
        assertEquals(request.getComment(), response.getComment());
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void testWhenDeleteReview() {
        // Arrange
        PetSitterReview review = new PetSitterReview(3, "SoSo~");

        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .user(requester)
                .endDate(LocalDateTime.MIN)
                .status(RequestStatus.ACCEPTED)
                .review(review)
                .build();
        when(petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId))
                .thenReturn(Optional.ofNullable(petSitterRequest));

        Long reviewId = 1L;

        // Act
        petSitterReviewService.delete(petSitterId, requestId, reviewId, requester);

        // Assert
        PetSitterReview review1 = petSitterRequest.getReview();
        assertEquals(true, review1.isDeleted());
    }
}