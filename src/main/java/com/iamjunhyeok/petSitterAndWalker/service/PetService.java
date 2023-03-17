package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.Gender;
import com.iamjunhyeok.petSitterAndWalker.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.domain.Pet;
import com.iamjunhyeok.petSitterAndWalker.domain.PetImage;
import com.iamjunhyeok.petSitterAndWalker.domain.PetProperty;
import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.ImageDto;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetAddRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetAddResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetListResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.PetPropertyDto;
import com.iamjunhyeok.petSitterAndWalker.repository.PetPropertyRepository;
import com.iamjunhyeok.petSitterAndWalker.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PetService {

    private final PetRepository petRepository;

    private final PetPropertyRepository petPropertyRepository;

    private final S3Service s3Service;

    public List<MyPetListResponse> getMyPets(User user) {
        return petRepository.findByUserId(user.getId()).stream()
                .map(pet -> buildMyPetViewResponse(pet))
                .collect(Collectors.toList());
    }

    private MyPetListResponse buildMyPetViewResponse(Pet pet) {
        return MyPetListResponse.builder()
                .id(pet.getId())
                .name(pet.getName())
                .petType(new PetPropertyDto(pet.getPetType().getId(), pet.getPetType().getName(), pet.getPetType().getOrder()))
                .images(buildImageDtoList(pet.getImages()))
                .build();
    }

    @Transactional
    public MyPetAddResponse addMyPet(MyPetAddRequest request, User user) {
        PetProperty petType = petPropertyRepository.findById(request.getPetTypeId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Pet type not found : %s", request.getPetTypeId())));
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

    @Transactional
    public void deleteMyPet(Long petId) {
        petRepository.deleteById(petId);
    }

    @Transactional
    public MyPetUpdateResponse updateMyPet(MyPetUpdateRequest request, Long petId) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new EntityNotFoundException(String.format("존재하지 않는 펫 ID : %d", petId)));
        pet.updatePetInfo(request.getName(), request.getBreed(), request.getAge(), request.isNeutered(), request.getWeight(), request.getDescription());

        List<Image> deleteImages = s3Service.deleteImageById(request.getDeleteImageIds());
        pet.deleteImage(deleteImages);

        List<Image> images = s3Service.uploadImage(request.getImages());
        pet.addImage(images);

        return MyPetUpdateResponse.builder()
                .id(pet.getId())
                .name(pet.getName())
                .breed(pet.getBreed())
                .age(pet.getAge())
                .gender(pet.getGender().name())
                .isNeutered(pet.isNeutered())
                .weight(pet.getWeight())
                .description(pet.getDescription())
                .petType(new PetPropertyDto(pet.getPetType().getId(), pet.getPetType().getName(), pet.getPetType().getOrder()))
                .images(buildImageDtoList(pet.getImages()))
                .build();
    }

    private List<ImageDto> buildImageDtoList(List<PetImage> images) {
        return images.stream()
                .map(PetImage::getImage)
                .map(image -> new ImageDto(image.getId(), image.getName()))
                .collect(Collectors.toList());
    }
}
