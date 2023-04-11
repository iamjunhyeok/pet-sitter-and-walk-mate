package com.iamjunhyeok.petSitterAndWalker.petSitter.service;

import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitter;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterInfoResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterListResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.repository.PetSitterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PetSitterService {

    private final PetSitterRepository petSitterRepository;

    private final PetSitterMapper petSitterMapper;

    public Page<PetSitterListResponse> getPetSitters(Pageable pageable) {
        Page<PetSitter> petSitters = petSitterRepository.findAll(pageable);
        return petSitters.map(petSitter -> PetSitterListResponse.builder()
                .name(petSitter.getUser().getName())
                .address(petSitter.getUser().getAddress1())
                .averageRating(petSitter.getAverageRating())
                .images(petSitterMapper.buildImageDtoList(petSitter.getImages()))
                .build());
    }

    public PetSitterInfoResponse getPetSitter(Long petSitterId) {
        log.info("Pet Sitter ID 로 펫 시터 정보 조회 : {}", petSitterId);
        PetSitter petSitter = petSitterRepository.findById(petSitterId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Pet Sitter ID 로 유효한 펫 시터 정보가 존재하지 않음 : %s", petSitterId)));
        log.info("성공적으로 조회 됨 : {}", petSitter.getId());
        return PetSitterInfoResponse.builder()
                .id(petSitter.getId())
                .name(petSitter.getUser().getName())
                .address(petSitter.getUser().getAddress1())
                .introduction(petSitter.getIntroduction())
                .reviews(petSitter.getReviews())
                .averageRating(petSitter.getAverageRating())
                .petTypes(petSitterMapper.buildPetTypeDtoList(petSitter.getPetTypes()))
                .petSizes(petSitterMapper.buildPetSizeDtoList(petSitter.getPetSizes()))
                .options(petSitterMapper.buildPetSitterOptionDtoList(petSitter.getOptions()))
                .images(petSitterMapper.buildImageDtoList(petSitter.getImages()))
                .build();
    }
}