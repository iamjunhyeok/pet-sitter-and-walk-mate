package com.iamjunhyeok.petSitterAndWalker.petSitter.service;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.RequestStatus;
import com.iamjunhyeok.petSitterAndWalker.image.dto.ImageSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.pet.domain.Pet;
import com.iamjunhyeok.petSitterAndWalker.pet.domain.PetImage;
import com.iamjunhyeok.petSitterAndWalker.pet.dto.PetSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.pet.repository.PetRepository;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitter;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterOption;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterRequestOption;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterOptionSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterRequestDto;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.repository.PetSitterOptionRepository;
import com.iamjunhyeok.petSitterAndWalker.petSitter.repository.PetSitterRepository;
import com.iamjunhyeok.petSitterAndWalker.petSitter.repository.PetSitterRequestRepository;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PetSitterRequestService {

    private final PetSitterRepository petSitterRepository;

    private final PetSitterRequestRepository petSitterRequestRepository;

    private final RedissonClient redissonClient;

    private final PetRepository petRepository;

    private final PetSitterOptionRepository petSitterOptionRepository;

    @Transactional
    public PetSitterResponse requestToPetSitter(PetSitterRequestDto request, Long petSitterId, User user) {
        log.info("펫 시터에게 펫 보호 요청 : {}", petSitterId);
        PetSitter petSitter = petSitterRepository.findById(petSitterId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("펫 시터가 존재하지 않음 : %s", petSitterId)));

        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .message(request.getMessage())
                .status(RequestStatus.REQUESTED)
                .user(user)
                .petSitter(petSitter)
                .build();
        petSitterRequestRepository.save(petSitterRequest);

        List<Pet> pets = petRepository.findAllById(request.getPetIds());
        petSitterRequest.addPet(pets);

        List<PetSitterOption> options = petSitterOptionRepository.findAllById(request.getOptionIds());
        petSitterRequest.addOption(options);

        log.info("해당 요청 ID 로 성공적으로 요청 됨 : {}", petSitterRequest.getId());

        return PetSitterResponse.builder()
                .id(petSitterRequest.getId())
                .startDate(petSitterRequest.getStartDate())
                .endDate(petSitterRequest.getEndDate())
                .message(petSitterRequest.getMessage())
                .pets(convertToPetSimpleDtoList(pets))
                .options(buildPetSitterRequestOptionDtoList(petSitterRequest.getOptions()))
                .build();
    }

    @NotNull
    private static List<PetSimpleDto> convertToPetSimpleDtoList(List<Pet> pets) {
        return pets.stream()
                .map(pet -> new PetSimpleDto(
                                pet.getId(),
                                pet.getName(),
                                pet.getImages().stream()
                                        .map(PetImage::getImage)
                                        .map(image -> new ImageSimpleDto(image.getId(), image.getName()))
                                        .findFirst()
                                        .orElseThrow(() -> new EntityNotFoundException(String.format("애완동물에 등록된 이미지가 존재하지 않음 : %s", pet.getId())))
                        )
                )
                .toList();
    }

    private List<PetSitterOptionSimpleDto> buildPetSitterRequestOptionDtoList(List<PetSitterRequestOption> options) {
        return options.stream()
                .map(PetSitterRequestOption::getPetSitterOption)
                .map(petSitterOption -> new PetSitterOptionSimpleDto(petSitterOption.getId(), petSitterOption.getName(), petSitterOption.getPrice()))
                .toList();
    }

    @Transactional
    public void acceptRequest(Long petSitterId, Long requestId, User user) {
        log.info("펫 시터가 해당 요청에 대한 수락을 진행 : {}", requestId);
        PetSitterRequest petSitterRequest = processPetSitterRequest(petSitterId, requestId, user);
        petSitterRequest.accept();
        log.info("펫 시터가 해당 요청에 대한 수락 완료 : {}", requestId);
    }

    @Transactional
    public void rejectRequest(Long petSitterId, Long requestId, User user) {
        log.info("펫 시터가 해당 요청에 대한 거절을 진행 : {}", requestId);
        PetSitterRequest petSitterRequest = processPetSitterRequest(petSitterId, requestId, user);
        petSitterRequest.reject();
        log.info("펫 시터가 해당 요청에 대한 거절 완료 : {}", requestId);
    }

    @Transactional
    public void cancelRequest(Long petSitterId, Long requestId, User user) {
        log.info("요청자가 펫 시터 요청 정보를 취소 진행 : {}", requestId);
        PetSitterRequest petSitterRequest = processRequesterRequest(petSitterId, requestId, user);
        petSitterRequest.cancel();
        log.info("요청자가 펫 시터 요청 정보를 취소 완료 : {}", requestId);
    }

    private PetSitterRequest processPetSitterRequest(Long petSitterId, Long requestId, User user) {
        String lockName = "request:" + requestId;
        RLock lock = redissonClient.getLock(lockName);
        lock.lock();
        log.info("Lock 획득 : {}", lockName);
        try {
            PetSitterRequest petSitterRequest = petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Request ID 로 등록된 요청 정보가 존재하지 않음 : %s", requestId)));

            if (!petSitterRequest.getPetSitter().getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException(String.format("로그인된 사용자가 수행할 수 없는 요청 정보 : %s", requestId));
            }
            return petSitterRequest;

        } finally {
            lock.unlock();
            log.info("Lock 해제 : {}", lockName);
        }
    }

    private PetSitterRequest processRequesterRequest(Long petSitterId, Long requestId, User user) {
        String lockName = "request:" + requestId;
        RLock lock = redissonClient.getLock(lockName);
        lock.lock();
        log.info("Lock 획득 : {}", lockName);
        try {
            PetSitterRequest petSitterRequest = petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Request ID 로 등록된 요청 정보가 존재하지 않음 : %s", requestId)));

            if (!petSitterRequest.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException(String.format("로그인된 사용자가 수행할 수 없는 요청 정보 : %s", requestId));
            }
            return petSitterRequest;

        } finally {
            lock.unlock();
            log.info("Lock 해제 : {}", lockName);
        }
    }
}
