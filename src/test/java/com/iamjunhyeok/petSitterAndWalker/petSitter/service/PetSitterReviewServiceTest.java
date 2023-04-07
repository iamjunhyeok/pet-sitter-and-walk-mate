package com.iamjunhyeok.petSitterAndWalker.petSitter.service;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.RequestStatus;
import com.iamjunhyeok.petSitterAndWalker.image.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.image.service.S3Service;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterReview;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewListResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetSitterReviewServiceTest {

    @InjectMocks
    private PetSitterReviewService petSitterReviewService;

    @Mock
    private PetSitterRequestRepository petSitterRequestRepository;

    @Mock
    private PetSitterReviewRepository petSitterReviewRepository;

    @Mock
    private S3Service s3Service;

    private Long petSitterId = 1L;
    private Long requestId = 1L;
    private User requester;
    private User otherUser;
    private List<MultipartFile> files = new ArrayList<>();
    private List<Image> images = new ArrayList<>();

    @BeforeEach
    void setup() {
        requester = User.builder()
                .id(1L)
                .build();
        otherUser = User.builder()
                .id(2L)
                .build();
        files.add(new MockMultipartFile("files", "image1.png", MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{}));
        images.add(new Image("image1.png"));
    }

    @Test
    @DisplayName("리뷰 등록 실패 - 펫 시터에 대한 요청이 아닌 경우")
    void testWhenNotRequestForPetSitter() {
        // Arrange
        when(petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> petSitterReviewService.register(new ReviewRegisterRequest(), files, petSitterId, requestId, new User()));
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
        assertThrows(AccessDeniedException.class, () -> petSitterReviewService.register(request, files, petSitterId, requestId, otherUser));
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
        assertThrows(IllegalStateException.class,
                () -> petSitterReviewService.register(request, files, petSitterId, requestId, requester));
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
        assertThrows(IllegalStateException.class, () -> petSitterReviewService.register(request, files, petSitterId, requestId, requester));
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
        when(s3Service.uploadImage(files)).thenReturn(images);

        // Act
        ReviewRegisterResponse response = petSitterReviewService.register(request, files, petSitterId, requestId, requester);

        // Assert
        assertNotNull(response);
        assertEquals(request.getRating(), response.getRating());
        assertEquals(request.getComment(), response.getComment());
        assertEquals(files.size(), response.getImages().size());
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
        assertThrows(EntityNotFoundException.class, () -> petSitterReviewService.update(request, files, petSitterId, requestId, reviewId, requester));
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void testWhenValidInputUpdate() {
        // Arrange
        PetSitterReview review = new PetSitterReview(3, "SoSo~");
        review.addImages(images);

        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .user(requester)
                .endDate(LocalDateTime.MIN)
                .status(RequestStatus.ACCEPTED)
                .review(review)
                .build();
        ReviewUpdateRequest request = new ReviewUpdateRequest(4, "Great!!");
        Long reviewId = 1L;

        when(petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId))
                .thenReturn(Optional.ofNullable(petSitterRequest));
        when(s3Service.deleteImageById(any())).thenReturn(images);

        // Act
        ReviewUpdateResponse response = petSitterReviewService.update(request, files, petSitterId, requestId, reviewId, requester);

        // Assert
        assertEquals(request.getRating(), response.getRating());
        assertEquals(request.getComment(), response.getComment());
        assertEquals(0, response.getImages().size());
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

    @Test
    @DisplayName("펫 시터 리뷰 목록")
    void testPetSitterReviewList() {
        // Arrange
        User requester = User.builder()
                .id(1L)
                .name("requester")
                .build();
        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .user(requester)
                .status(RequestStatus.ACCEPTED)
                .endDate(LocalDateTime.MIN)
                .build();

        PetSitterReview petSitterReview = new PetSitterReview(3, "SoSo~");
        petSitterReview.addImages(images);

        petSitterRequest.registerReview(petSitterReview);

        User requester2 = User.builder()
                .id(1L)
                .name("requester")
                .build();
        PetSitterRequest petSitterRequest2 = PetSitterRequest.builder()
                .user(requester2)
                .status(RequestStatus.ACCEPTED)
                .endDate(LocalDateTime.MIN)
                .build();
        PetSitterReview petSitterReview2 = new PetSitterReview(5, "Great!!");
        petSitterReview2.addImages(images);

        petSitterRequest2.registerReview(petSitterReview2);

        List<PetSitterReview> reviewList = Arrays.asList(petSitterReview, petSitterReview2);

        PageRequest pageRequest = PageRequest.of(10, 10);
        when(petSitterReviewRepository.findAllByPetSitterId(petSitterId, pageRequest))
                .thenReturn(new PageImpl<>(reviewList));

        // Act
        Page<ReviewListResponse> response = petSitterReviewService.reviewList(pageRequest, petSitterId);

        // Assert
        assertEquals(reviewList.size(), response.getSize());
        assertEquals(reviewList.get(0).getRating(), response.getContent().get(0).getRating());
        assertEquals(reviewList.get(0).getComment(), response.getContent().get(0).getComment());
    }

    @Test
    @DisplayName("펫 시터 리뷰 조회")
    void testPetSitter() {
        // Arrange
        User requester = User.builder()
                .id(1L)
                .name("requester")
                .build();
        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .user(requester)
                .status(RequestStatus.ACCEPTED)
                .endDate(LocalDateTime.MIN)
                .build();

        Long reviewId = 1L;
        PetSitterReview petSitterReview = new PetSitterReview(3, "SoSo~");
        petSitterReview.addImages(images);

        petSitterRequest.registerReview(petSitterReview);

        when(petSitterReviewRepository.findByIdAndPetSitterId(reviewId, petSitterId))
                .thenReturn(Optional.of(petSitterReview));

        // Act
        ReviewResponse response = petSitterReviewService.review(petSitterId, reviewId);

        // Assert
        assertEquals(petSitterReview.getRating(), response.getRating());
        assertEquals(petSitterReview.getComment(), response.getComment());
        assertEquals(requester.getId(), response.getUser().getId());
        assertEquals(requester.getName(), response.getUser().getName());
        assertEquals(images.get(0).getName(), response.getImages().get(0).getName());
    }
}