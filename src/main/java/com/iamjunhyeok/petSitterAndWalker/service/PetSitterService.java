package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.domain.PetProperty;
import com.iamjunhyeok.petSitterAndWalker.domain.PetSitter;
import com.iamjunhyeok.petSitterAndWalker.domain.PetSitterOption;
import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterOptionResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.repository.PetPropertyRepository;
import com.iamjunhyeok.petSitterAndWalker.repository.PetSitterRepository;
import com.iamjunhyeok.petSitterAndWalker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PetSitterService {

    private final UserRepository userRepository;

    private final PetPropertyRepository petPropertyRepository;

    private final PetSitterRepository petSitterRepository;

    private final S3Service s3Service;

    @Transactional
    public PetSitterRegisterResponse register(PetSitterRegisterRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Cannot find user with userId : %d", userId)));

        List<PetSitterOption> options = request.getOptions().stream()
                .map(option -> new PetSitterOption(option.getName(), option.getDescription(), option.getPrice()))
                .collect(Collectors.toList());

        PetSitter petSitter = new PetSitter(request.getIntro(), request.getExperience());
        PetSitter save = petSitterRepository.save(petSitter);
        save.addOption(options);

        List<PetProperty> petTypes = petPropertyRepository.findAllById(request.getPetTypeId());
        save.addPetType(petTypes);

        List<PetProperty> petSizes = petPropertyRepository.findAllById(request.getPetSizeId());
        save.addPetSize(petSizes);

        List<Image> images = s3Service.uploadImage(request.getImages());
        save.addImage(images);

        user.registerPetSitterInfo(save);

        return PetSitterRegisterResponse.builder()
                .images(images.stream().map(Image::getName).collect(Collectors.toList()))
                .intro(petSitter.getIntro())
                .experience(petSitter.getExperience())
                .petTypes(petTypes.stream().map(PetProperty::getName).collect(Collectors.toList()))
                .petSizes(petSizes.stream().map(PetProperty::getName).collect(Collectors.toList()))
                .options(options.stream().map(petSitterOption ->
                        new PetSitterOptionResponse(
                                petSitterOption.getName(),
                                petSitterOption.getDescription(),
                                petSitterOption.getPrice())).collect(Collectors.toList()
                        )
                )
                .build();
    }
}
