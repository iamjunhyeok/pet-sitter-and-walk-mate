package com.iamjunhyeok.petSitterAndWalker.petSitter.service;

import com.iamjunhyeok.petSitterAndWalker.image.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.image.dto.ImageSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.image.service.S3Service;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterReview;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterReviewImage;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewListResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.ReviewUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.repository.PetSitterRequestRepository;
import com.iamjunhyeok.petSitterAndWalker.petSitter.repository.PetSitterReviewRepository;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import com.iamjunhyeok.petSitterAndWalker.user.dto.UserSimpleDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PetSitterReviewService {

    private final PetSitterRequestRepository petSitterRequestRepository;

    private final PetSitterReviewRepository petSitterReviewRepository;

    private final S3Service s3Service;

    @Transactional
    public ReviewRegisterResponse register(ReviewRegisterRequest request, List<MultipartFile> files, Long petSitterId, Long requestId, User user) {
        log.info("펫 시터 리뷰 등록 시도 : {}", requestId);
        PetSitterRequest petSitterRequest = getPetSitterRequest(petSitterId, requestId, user);

        PetSitterReview petSitterReview = new PetSitterReview(request.getRating(), request.getComment());
        petSitterReviewRepository.save(petSitterReview);

        petSitterRequest.registerReview(petSitterReview);

        List<Image> images = s3Service.uploadImage(files);
        petSitterReview.addImages(images);

        log.info("펫 시터 리뷰 등록 성공 : {}", requestId);

        ReviewRegisterResponse response = new ReviewRegisterResponse(petSitterReview.getId(), petSitterReview.getRating(), petSitterReview.getComment());
        response.setImages(images.stream().map(image -> new ImageSimpleDto(image.getId(), image.getName())).toList());
        return response;
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
    public ReviewUpdateResponse update(ReviewUpdateRequest request, List<MultipartFile> files, Long petSitterId, Long requestId, Long reviewId, User user) {
        log.info("펫 시터 리뷰 수정 시도 : {}", reviewId);
        PetSitterRequest petSitterRequest = getPetSitterRequest(petSitterId, requestId, user);

        PetSitterReview review = petSitterRequest.getReview();
        if (review == null) {
            throw new EntityNotFoundException(String.format("리뷰가 존재하지 않음", reviewId));
        }

        review.update(request.getRating(), request.getComment());

        List<Image> deletedImages = s3Service.deleteImageById(request.getDeleteImageIds());
        review.deleteImages(deletedImages);

        List<Image> images = s3Service.uploadImage(files);
        review.addImages(images);


        ReviewUpdateResponse response = new ReviewUpdateResponse();
        BeanUtils.copyProperties(review, response);

        response.setImages(review.getImages().stream().map(PetSitterReviewImage::getImage).map(image -> new ImageSimpleDto(image.getId(), image.getName())).toList());

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

    public Page<ReviewListResponse> reviewList(Pageable pageable, Long petSitterId) {
        log.info("펫 시터에게 등록된 리뷰 목록 조회 : {}", petSitterId);
        Page<PetSitterReview> reviewList = petSitterReviewRepository.findAllByPetSitterId(petSitterId, pageable);
        List<ReviewListResponse> responses = reviewList.map(petSitterReview -> {
            ReviewListResponse res = new ReviewListResponse(petSitterReview.getId(), petSitterReview.getRating(), petSitterReview.getComment());

            User requester = petSitterReview.getRequest().getUser();
            UserSimpleDto userSimpleDto = new UserSimpleDto(requester.getId(), requester.getName());

            List<ImageSimpleDto> imageSimpleDtos = petSitterReview.getImages().stream()
                    .map(PetSitterReviewImage::getImage)
                    .map(image -> new ImageSimpleDto(image.getId(), image.getName()))
                    .toList();

            res.setUser(userSimpleDto);
            res.setImages(imageSimpleDtos);
            return res;
        }).toList();

        log.info("펫 시터에게 등록된 리뷰 목록 조회 성공 : {}", petSitterId);
        return new PageImpl<>(responses);
    }

    public ReviewResponse review(Long petSitterId, Long reviewId) {
        log.info("펫 시터에게 등록된 리뷰 조회 : {}", reviewId);

        PetSitterReview petSitterReview = petSitterReviewRepository.findByIdAndPetSitterId(reviewId, petSitterId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("리뷰가 존재하지 않음 : %s", reviewId)));

        ReviewResponse response = new ReviewResponse();
        BeanUtils.copyProperties(petSitterReview, response);

        User requester = petSitterReview.getRequest().getUser();
        UserSimpleDto userSimpleDto = new UserSimpleDto(requester.getId(), requester.getName());

        List<ImageSimpleDto> imageSimpleDtos = petSitterReview.getImages().stream()
                .map(PetSitterReviewImage::getImage)
                .map(image -> new ImageSimpleDto(image.getId(), image.getName()))
                .toList();

        response.setUser(userSimpleDto);
        response.setImages(imageSimpleDtos);

        log.info("펫 시터에게 등록된 리뷰 조회 성공 : {}", reviewId);
        return response;
    }
}
