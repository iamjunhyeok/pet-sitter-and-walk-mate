package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.domain.PetProperty;
import com.iamjunhyeok.petSitterAndWalker.domain.PetSitter;
import com.iamjunhyeok.petSitterAndWalker.domain.PetSitterImage;
import com.iamjunhyeok.petSitterAndWalker.domain.PetSitterOption;
import com.iamjunhyeok.petSitterAndWalker.domain.PetSitterPetSize;
import com.iamjunhyeok.petSitterAndWalker.domain.PetSitterPetType;
import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.ImageDto;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetSitterInfoRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetSitterInfoUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetSitterInfoUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetSitterInfoViewResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.PetPropertyDto;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterOptionDto;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterOptionRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.repository.ImageRepository;
import com.iamjunhyeok.petSitterAndWalker.repository.PetPropertyRepository;
import com.iamjunhyeok.petSitterAndWalker.repository.PetSitterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PetSitterService {

    private final PetPropertyRepository petPropertyRepository;

    private final PetSitterRepository petSitterRepository;

    private final ImageRepository imageRepository;

    private final S3Service s3Service;

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

        List<Image> deleteImages = imageRepository.findAllById(request.getDeleteImageIds());
        s3Service.delete(deleteImages);
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

    private List<PetPropertyDto> buildPetTypeDtoList(List<PetSitterPetType> petSizes) {
        return petSizes.stream()
                .map(PetSitterPetType::getPetProperty)
                .map(petProperty -> new PetPropertyDto(petProperty.getId(), petProperty.getName(), petProperty.getOrder()))
                .collect(Collectors.toList());
    }

    private List<PetPropertyDto> buildPetSizeDtoList(List<PetSitterPetSize> petSizes) {
        return petSizes.stream()
                .map(PetSitterPetSize::getPetProperty)
                .map(petProperty -> new PetPropertyDto(petProperty.getId(), petProperty.getName(), petProperty.getOrder()))
                .collect(Collectors.toList());
    }

    private List<PetSitterOptionDto> buildPetSitterOptionDtoList(List<PetSitterOption> options) {
        return options.stream()
                .map(petSitterOption -> new PetSitterOptionDto(petSitterOption.getId(), petSitterOption.getName(), petSitterOption.getPrice()))
                .collect(Collectors.toList());
    }

    private List<ImageDto> buildImageDtoList(List<PetSitterImage> images) {
        return images.stream()
                .map(PetSitterImage::getImage)
                .map(image -> new ImageDto(image.getId(), image.getName()))
                .collect(Collectors.toList());
    }
}