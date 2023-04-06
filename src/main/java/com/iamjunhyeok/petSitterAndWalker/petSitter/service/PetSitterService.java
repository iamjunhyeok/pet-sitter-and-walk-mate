package com.iamjunhyeok.petSitterAndWalker.petSitter.service;

import com.iamjunhyeok.petSitterAndWalker.image.dto.ImageSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.pet.dto.PetPropertySimpleDto;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitter;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterImage;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterOption;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterPetSize;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterPetType;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterInfoResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterListResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterOptionSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.petSitter.repository.PetSitterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PetSitterService {

    private final PetSitterRepository petSitterRepository;

    public Page<PetSitterListResponse> getPetSitters(Pageable pageable) {
        Page<PetSitter> petSitters = petSitterRepository.findAll(pageable);
        return petSitters.map(petSitter -> PetSitterListResponse.builder()
                .name(petSitter.getUser().getName())
                .address(petSitter.getUser().getAddress1())
                .averageRating(petSitter.getAverageRating())
                .images(buildImageDtoList(petSitter.getImages()))
                .build());
    }

    public PetSitterInfoResponse getPetSitter(Long petSitterId) {
        log.info("Pet Sitter ID 로 펫 시터 정보 조회 : {}", petSitterId);
        PetSitter petSitter = petSitterRepository.findById(petSitterId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Pet Sitter ID 로 유효한 펫 시터 정보가 존재하지 않음 : %s", petSitterId)));
        log.info("성공적으로 조회 됨 : {}", petSitter.getId());
        return PetSitterInfoResponse.builder()
                .name(petSitter.getUser().getName())
                .address(petSitter.getUser().getAddress1())
//                .profileImage(petSitter.getUser().getImage())
                .introduction(petSitter.getIntroduction())
                .reviews(petSitter.getReviews())
                .averageRating(petSitter.getAverageRating())
                .petTypes(buildPetTypeDtoList(petSitter.getPetTypes()))
                .petSizes(buildPetSizeDtoList(petSitter.getPetSizes()))
                .options(buildPetSitterOptionDtoList(petSitter.getOptions()))
                .images(buildImageDtoList(petSitter.getImages()))
//                .recentReviews()
                .build();

    }

    private List<PetPropertySimpleDto> buildPetTypeDtoList(List<PetSitterPetType> petSizes) {
        return petSizes.stream()
                .map(PetSitterPetType::getPetProperty)
                .map(petProperty -> new PetPropertySimpleDto(petProperty.getId(), petProperty.getName(), petProperty.getOrder()))
                .toList();
    }

    private List<PetPropertySimpleDto> buildPetSizeDtoList(List<PetSitterPetSize> petSizes) {
        return petSizes.stream()
                .map(PetSitterPetSize::getPetProperty)
                .map(petProperty -> new PetPropertySimpleDto(petProperty.getId(), petProperty.getName(), petProperty.getOrder()))
                .toList();
    }

    private List<PetSitterOptionSimpleDto> buildPetSitterOptionDtoList(List<PetSitterOption> options) {
        return options.stream()
                .map(petSitterOption -> new PetSitterOptionSimpleDto(petSitterOption.getId(), petSitterOption.getName(), petSitterOption.getPrice()))
                .toList();
    }

    private List<ImageSimpleDto> buildImageDtoList(List<PetSitterImage> images) {
        return images.stream()
                .map(PetSitterImage::getImage)
                .map(image -> new ImageSimpleDto(image.getId(), image.getName()))
                .toList();
    }
}