package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.Gender;
import com.iamjunhyeok.petSitterAndWalker.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.domain.Pet;
import com.iamjunhyeok.petSitterAndWalker.domain.PetProperty;
import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.ImageDto;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetAddRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetAddResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.PetPropertyDto;
import com.iamjunhyeok.petSitterAndWalker.dto.PetViewResponse;
import com.iamjunhyeok.petSitterAndWalker.repository.PetPropertyRepository;
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

    private final PetPropertyRepository petPropertyRepository;

    private final S3Service s3Service;

    public MyPetAddResponse addMyPet(MyPetAddRequest request, User user) {
        PetProperty petType = petPropertyRepository.findById(request.getPetTypeId()).orElseThrow(() -> new EntityNotFoundException(String.format("Pet type not found : %s", request.getPetTypeId())));
        Pet pet = Pet.builder()
                .name(request.getName())
                .breed(request.getBreed())
                .age(request.getAge())
                .gender(Gender.valueOf(request.getGender()))
                .isNeutered(request.isNeutered())
                .weight(request.getWeight())
                .description(request.getDescription())
                .petType(petType)
                .build();
        Pet save = petRepository.save(pet);
        user.addPet(save);

        List<Image> images = s3Service.uploadImage(request.getImages());
        save.addImage(images);

        return MyPetAddResponse.builder()
                .id(save.getId())
                .name(save.getName())
                .breed(save.getBreed())
                .age(save.getAge())
                .gender(save.getGender().name())
                .isNeutered(save.isNeutered())
                .weight(save.getWeight())
                .description(save.getDescription())
                .petType(new PetPropertyDto(petType.getId(), petType.getName(), petType.getOrder()))
                .images(images.stream().map(image -> new ImageDto(image.getId(), image.getName())).collect(Collectors.toList()))
                .build();
    }

    public List<PetViewResponse> getUserPets(Long userId) {
        return petRepository.findByUserId(userId).stream()
                .map(pet -> new PetViewResponse(pet.getId(), pet.getName(), pet.getRepresentativeImage().getName()))
                .collect(Collectors.toList());
    }
}
