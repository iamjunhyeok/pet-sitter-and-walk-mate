package com.iamjunhyeok.petSitterAndWalker.petSitter.service;

import com.iamjunhyeok.petSitterAndWalker.image.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.image.service.S3Service;
import com.iamjunhyeok.petSitterAndWalker.pet.domain.PetProperty;
import com.iamjunhyeok.petSitterAndWalker.pet.repository.PetPropertyRepository;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitter;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterOption;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.MyPetSitterInfoRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.MyPetSitterInfoUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.MyPetSitterInfoUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.MyPetSitterInfoViewResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterOptionRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.repository.PetSitterRepository;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import com.iamjunhyeok.petSitterAndWalker.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PetSitterProfileService {

    private final PetSitterRepository petSitterRepository;

    private final PetPropertyRepository petPropertyRepository;

    private final S3Service s3Service;

    private final UserRepository userRepository;

    public MyPetSitterInfoViewResponse viewMyPetSitterInfo(User user) {
        PetSitter petSitter = petSitterRepository.findByUserId(user.getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("There is no registered pet sitter information : %s", user.getId())));
        return null;
//        return MyPetSitterInfoViewResponse.builder()
//                .images(buildImageDtoList(petSitter.getImages()))
//                .introduction(petSitter.getIntroduction())
//                .isAvailable(petSitter.isAvailable())
//                .petTypes(buildPetTypeDtoList(petSitter.getPetTypes()))
//                .petSizes(buildPetSizeDtoList(petSitter.getPetSizes()))
//                .options(buildPetSitterOptionDtoList(petSitter.getOptions()))
//                .build();
    }

    @Transactional
    public PetSitterRegisterResponse registerMyPetSitterInfo(MyPetSitterInfoRegisterRequest request, User user) {
        User findUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("존재하지 않는 사용자 : %s", user.getId())));

        PetSitter petSitter = getPetSitter(request);

        findUser.registerPetSitterInfo(petSitter);

        return null;
//        return PetSitterRegisterResponse.builder()
//                .introduction(petSitter.getIntroduction())
//                .petTypes(buildPetTypeDtoList(petSitter.getPetTypes()))
//                .petSizes(buildPetSizeDtoList(petSitter.getPetSizes()))
//                .options(buildPetSitterOptionDtoList(petSitter.getOptions()))
//                .images(buildImageDtoList(petSitter.getImages()))
//                .build();
    }


    @Transactional
    public MyPetSitterInfoUpdateResponse updateMyPetSitterInfo(MyPetSitterInfoUpdateRequest request, User user) {
        PetSitter petSitter = getPetSitter(request, user);

        return null;
//        return MyPetSitterInfoUpdateResponse.builder()
//                .images(buildImageDtoList(petSitter.getImages()))
//                .introduction(petSitter.getIntroduction())
//                .isAvailable(petSitter.isAvailable())
//                .petTypes(buildPetTypeDtoList(petSitter.getPetTypes()))
//                .petSizes(buildPetSizeDtoList(petSitter.getPetSizes()))
//                .options(buildPetSitterOptionDtoList(petSitter.getOptions()))
//                .build();
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

        petSitterRepository.save(petSitter);

        return petSitter;
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

        List<Image> deleteImages = s3Service.deleteImageById(request.getDeleteImageIds());
        petSitter.deleteImage(deleteImages);

        List<Image> images = s3Service.uploadImage(request.getImages());
        petSitter.addImage(images);

        return petSitter;
    }

    @NotNull
    private static List<PetSitterOption> getPetSitterOptions(List<PetSitterOptionRequest> request) {
        return Optional.ofNullable(request)
                .orElse(Collections.emptyList())
                .stream()
                .map(option -> new PetSitterOption(option.getName(), option.getDescription(), option.getPrice()))
                .toList();
    }
}
