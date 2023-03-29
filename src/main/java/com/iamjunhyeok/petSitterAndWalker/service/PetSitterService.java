package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.RequestStatus;
import com.iamjunhyeok.petSitterAndWalker.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.domain.Pet;
import com.iamjunhyeok.petSitterAndWalker.domain.PetImage;
import com.iamjunhyeok.petSitterAndWalker.domain.PetProperty;
import com.iamjunhyeok.petSitterAndWalker.domain.PetSitter;
import com.iamjunhyeok.petSitterAndWalker.domain.PetSitterImage;
import com.iamjunhyeok.petSitterAndWalker.domain.PetSitterOption;
import com.iamjunhyeok.petSitterAndWalker.domain.PetSitterPetSize;
import com.iamjunhyeok.petSitterAndWalker.domain.PetSitterPetType;
import com.iamjunhyeok.petSitterAndWalker.domain.PetSitterRequest;
import com.iamjunhyeok.petSitterAndWalker.domain.PetSitterRequestOption;
import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.ImageSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetSitterInfoRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetSitterInfoUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetSitterInfoUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetSitterInfoViewResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.PetPropertySimpleDto;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterInfoResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterListResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterOptionRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterOptionSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterRequestDto;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterResponse;
import com.iamjunhyeok.petSitterAndWalker.repository.ImageRepository;
import com.iamjunhyeok.petSitterAndWalker.repository.PetPropertyRepository;
import com.iamjunhyeok.petSitterAndWalker.repository.PetRepository;
import com.iamjunhyeok.petSitterAndWalker.repository.PetSitterOptionRepository;
import com.iamjunhyeok.petSitterAndWalker.repository.PetSitterRepository;
import com.iamjunhyeok.petSitterAndWalker.repository.PetSitterRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PetSitterService {
    private final PetSitterRequestRepository petSitterRequestRepository;

    private final PetPropertyRepository petPropertyRepository;

    private final PetSitterRepository petSitterRepository;

    private final ImageRepository imageRepository;

    private final PetRepository petRepository;

    private final PetSitterOptionRepository petSitterOptionRepository;

    private final S3Service s3Service;

    private final RedissonClient redissonClient;

    public MyPetSitterInfoViewResponse viewMyPetSitterInfo(User user) {
        PetSitter petSitter = petSitterRepository.findByUserId(user.getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("There is no registered pet sitter information : %s", user.getId())));
        return MyPetSitterInfoViewResponse.builder()
                .images(buildImageDtoList(petSitter.getImages()))
                .introduction(petSitter.getIntroduction())
                .isAvailable(petSitter.isAvailable())
                .petTypes(buildPetTypeDtoList(petSitter.getPetTypes()))
                .petSizes(buildPetSizeDtoList(petSitter.getPetSizes()))
                .options(buildPetSitterOptionDtoList(petSitter.getOptions()))
                .build();
    }

    @Transactional
    public PetSitterRegisterResponse registerMyPetSitterInfo(MyPetSitterInfoRegisterRequest request, User user) {
        PetSitter petSitter = getPetSitter(request);

        user.registerPetSitterInfo(petSitter);
        petSitterRepository.save(petSitter);

        return PetSitterRegisterResponse.builder()
                .introduction(petSitter.getIntroduction())
                .petTypes(buildPetTypeDtoList(petSitter.getPetTypes()))
                .petSizes(buildPetSizeDtoList(petSitter.getPetSizes()))
                .options(buildPetSitterOptionDtoList(petSitter.getOptions()))
                .images(buildImageDtoList(petSitter.getImages()))
                .build();
    }

    @NotNull
    private PetSitter getPetSitter(MyPetSitterInfoRegisterRequest request) {
        PetSitter petSitter = new PetSitter(request.getIntroduction());

        List<PetSitterOption> options = getPetSitterOptions(request.getOptions());
        petSitter.addOption(options);

        List<PetProperty> petTypes = petPropertyRepository.findAllById(request.getPetTypeIds());
        petSitter.addPetType(petTypes);

        List<PetProperty> petSizes = petPropertyRepository.findAllById(request.getPetSizeIds());
        petSitter.addPetSize(petSizes);

        List<Image> images = s3Service.uploadImage(request.getImages());
        petSitter.addImage(images);

        return petSitter;
    }

    @Transactional
    public MyPetSitterInfoUpdateResponse updateMyPetSitterInfo(MyPetSitterInfoUpdateRequest request, User user) {
        PetSitter petSitter = getPetSitter(request, user);

        return MyPetSitterInfoUpdateResponse.builder()
                .images(buildImageDtoList(petSitter.getImages()))
                .introduction(petSitter.getIntroduction())
                .isAvailable(petSitter.isAvailable())
                .petTypes(buildPetTypeDtoList(petSitter.getPetTypes()))
                .petSizes(buildPetSizeDtoList(petSitter.getPetSizes()))
                .options(buildPetSitterOptionDtoList(petSitter.getOptions()))
                .build();
    }

    @NotNull
    private PetSitter getPetSitter(MyPetSitterInfoUpdateRequest request, User user) {
        PetSitter petSitter = petSitterRepository.findByUserId(user.getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("User's pet sitter information does not exist. : %s", user.getId())));
        petSitter.changeIntroduction(request.getIntroduction());

        petSitter.deleteAllOptions();

        List<PetSitterOption> options = getPetSitterOptions(request.getOptions());
        petSitter.addOption(options);

        List<PetProperty> petTypes = petPropertyRepository.findAllById(request.getPetTypeIds());
        petSitter.clearAndAddPetTypes(petTypes);

        List<PetProperty> petSizes = petPropertyRepository.findAllById(request.getPetSizeIds());
        petSitter.clearAndAddPetSizes(petSizes);

        System.out.println("request.getDeleteImageIds() = " + request.getDeleteImageIds());
        
        List<Image> deleteImages = s3Service.deleteImageById(request.getDeleteImageIds());
        petSitter.deleteImage(deleteImages);

        List<Image> images = s3Service.uploadImage(request.getImages());
        petSitter.addImage(images);

        return petSitter;
    }

    @NotNull
    private static List<PetSitterOption> getPetSitterOptions(List<PetSitterOptionRequest> request) {
        List<PetSitterOption> options = Optional.ofNullable(request)
                .orElse(Collections.emptyList())
                .stream()
                .map(option -> new PetSitterOption(option.getName(), option.getDescription(), option.getPrice()))
                .collect(Collectors.toList());
        return options;
    }

    private List<PetPropertySimpleDto> buildPetTypeDtoList(List<PetSitterPetType> petSizes) {
        return petSizes.stream()
                .map(PetSitterPetType::getPetProperty)
                .map(petProperty -> new PetPropertySimpleDto(petProperty.getId(), petProperty.getName(), petProperty.getOrder()))
                .collect(Collectors.toList());
    }

    private List<PetPropertySimpleDto> buildPetSizeDtoList(List<PetSitterPetSize> petSizes) {
        return petSizes.stream()
                .map(PetSitterPetSize::getPetProperty)
                .map(petProperty -> new PetPropertySimpleDto(petProperty.getId(), petProperty.getName(), petProperty.getOrder()))
                .collect(Collectors.toList());
    }

    private List<PetSitterOptionSimpleDto> buildPetSitterOptionDtoList(List<PetSitterOption> options) {
        return options.stream()
                .map(petSitterOption -> new PetSitterOptionSimpleDto(petSitterOption.getId(), petSitterOption.getName(), petSitterOption.getPrice()))
                .collect(Collectors.toList());
    }

    private List<ImageSimpleDto> buildImageDtoList(List<PetSitterImage> images) {
        return images.stream()
                .map(PetSitterImage::getImage)
                .map(image -> new ImageSimpleDto(image.getId(), image.getName()))
                .collect(Collectors.toList());
    }

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

    @Transactional
    public PetSitterResponse petSitter(PetSitterRequestDto request, Long petSitterId, User user) {
        log.info("펫 시터에게 펫 보호 요청 : {}", petSitterId);
        PetSitter petSitter = petSitterRepository.findById(petSitterId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("펫 시터가 존재하지 않음", petSitterId)));

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
        List<PetSimpleDto> petSimpleDtos = pets.stream()
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
                .collect(Collectors.toList());
        return petSimpleDtos;
    }

    private List<PetSitterOptionSimpleDto> buildPetSitterRequestOptionDtoList(List<PetSitterRequestOption> options) {
        return options.stream()
                .map(PetSitterRequestOption::getPetSitterOption)
                .map(petSitterOption -> new PetSitterOptionSimpleDto(petSitterOption.getId(), petSitterOption.getName(), petSitterOption.getPrice()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptRequest(Long petSitterId, Long requestId, User user) {
        log.info("펫 시터가 해당 요청에 대한 수락을 진행 : {}", requestId);
        String lockName = "request:" + requestId;
        RLock lock = redissonClient.getLock(lockName);
        lock.lock();
        log.info("Lock 획득 : {}", lockName);
        try {
            PetSitterRequest petSitterRequest = petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Request ID 로 등록된 요청 정보가 존재하지 않음 : %s", requestId)));
            if (petSitterRequest.getPetSitter().getUser().getId() != user.getId()) {
                throw new AccessDeniedException(String.format("로그인된 사용자가 수행할 수 없는 요청 정보 : %s", requestId));
            }
            petSitterRequest.accept();
        } finally {
            lock.unlock();
            log.info("Lock 해제 : {}", lockName);
        }
        log.info("펫 시터가 해당 요청에 대한 수락 완료 : {}", requestId);
    }

    @Transactional
    public void rejectRequest(Long petSitterId, Long requestId, User user) {
        log.info("펫 시터가 해당 요청에 대한 거절을 진행 : {}", requestId);
        String lockName = "request:" + requestId;
        RLock lock = redissonClient.getLock(lockName);
        lock.lock();
        log.info("Lock 획득 : {}", lockName);
        try {
            PetSitterRequest petSitterRequest = petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Request ID 로 등록된 요청 정보가 존재하지 않음 : %s", requestId)));
            if (petSitterRequest.getPetSitter().getUser().getId() != user.getId()) {
                throw new AccessDeniedException(String.format("로그인된 사용자가 수행할 수 없는 요청 정보 : %s", requestId));
            }
            petSitterRequest.reject();
        } finally {
            lock.unlock();
            log.info("Lock 해제 : {}", lockName);
        }
        log.info("펫 시터가 해당 요청에 대한 거절 완료 : {}", requestId);
    }

    @Transactional
    public void cancelRequest(Long petSitterId, Long requestId, User user) {
        log.info("요청자가 펫 시터 요청 정보를 취소 진행 : {}", requestId);
        String lockName = "request:" + requestId;
        RLock lock = redissonClient.getLock(lockName);
        lock.lock();
        log.info("Lock 획득 : {}", lockName);
        try {
            PetSitterRequest petSitterRequest = petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Request ID 로 등록된 요청 정보가 존재하지 않음 : %s", requestId)));
            if (petSitterRequest.getUser().getId() != user.getId()) {
                throw new AccessDeniedException(String.format("로그인된 사용자가 수행할 수 없는 요청 정보 : %s", requestId));
            }
            petSitterRequest.cancel();
        } finally {
            lock.unlock();
            log.info("Lock 해제 : {}", lockName);
        }
        log.info("요청자가 펫 시터 요청 정보를 취소 완료 : {}", requestId);
    }
}