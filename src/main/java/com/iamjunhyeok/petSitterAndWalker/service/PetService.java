package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.constants.Gender;
import com.iamjunhyeok.petSitterAndWalker.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.domain.Pet;
import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.PetRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.PetRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.PetViewResponse;
import com.iamjunhyeok.petSitterAndWalker.repository.PetRepository;
import com.iamjunhyeok.petSitterAndWalker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class PetService {

    private final UserRepository userRepository;

    private final PetRepository petRepository;

    private final S3Service s3Service;

    public PetRegisterResponse register(Long userId, PetRegisterRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException());

        Pet pet = Pet.builder()
                .name(request.getName())
                .breed(request.getBreed())
                .age(request.getAge())
                .gender(Gender.valueOf(request.getGender()))
                .isNeutered(request.isNeutered())
                .weight(request.getWeight())
                .intro(request.getIntro())
                .build();
        Pet save = petRepository.save(pet);
        user.registerPet(save);

        List<Image> images = s3Service.uploadImage(request.getImages());
        save.addImage(images);

        return PetRegisterResponse.builder()
                .id(save.getId())
                .name(save.getName())
                .breed(save.getBreed())
                .age(save.getAge())
                .gender(save.getGender().name())
                .isNeutered(save.isNeutered())
                .weight(save.getWeight())
                .intro(save.getIntro())
                .images(images.stream().map(Image::getName).collect(Collectors.toList()))
                .build();
    }

    public List<PetViewResponse> getUserPets(Long userId) {
        return petRepository.findByUserId(userId).stream()
                .map(pet -> new PetViewResponse(pet.getId(), pet.getName(), pet.getRepresentativeImage().getName()))
                .collect(Collectors.toList());
    }
}
