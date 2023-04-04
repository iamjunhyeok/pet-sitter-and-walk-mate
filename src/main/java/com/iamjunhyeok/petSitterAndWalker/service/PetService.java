package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.Gender;
import com.iamjunhyeok.petSitterAndWalker.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.domain.Pet;
import com.iamjunhyeok.petSitterAndWalker.domain.PetImage;
import com.iamjunhyeok.petSitterAndWalker.domain.PetProperty;
import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.ImageSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetAddRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetAddResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetListResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetViewResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.PetPropertySimpleDto;
import com.iamjunhyeok.petSitterAndWalker.repository.PetPropertyRepository;
import com.iamjunhyeok.petSitterAndWalker.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PetService {

    private final PetRepository petRepository;

    private final PetPropertyRepository petPropertyRepository;

    private final S3Service s3Service;

    public List<MyPetListResponse> getMyPets(User user) {
        log.info("애완동물 목록 조회 : {}", user.getId());
        List<MyPetListResponse> response = petRepository.findByUserId(user.getId()).stream()
                .map(pet -> {
                    MyPetListResponse res = new MyPetListResponse();
                    BeanUtils.copyProperties(pet, res);

                    res.setPetType(buildPetPropertySimpleDto(pet.getPetType()));
                    res.setImages(buildImageDtoList(pet.getImages()));
                    return res;
                })
                .toList();
        log.info("애완동물 목록 조회 성공 : {}", user.getId());
        return response;
    }

    @Transactional
    public MyPetAddResponse addMyPet(MyPetAddRequest request, User user) {
        log.info("애완동물 추가 : {}", user.getId());
        PetProperty petType = petPropertyRepository.findById(request.getPetTypeId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("펫 타입이 존재하지 않음 : %s", request.getPetTypeId())));
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

        MyPetAddResponse response = new MyPetAddResponse();
        BeanUtils.copyProperties(save, response);

        response.setPetType(buildPetPropertySimpleDto(petType));
        response.setImages(buildImageDtoList(save.getImages()));

        log.info("애완동물 추가 성공 : {}", user.getId());
        return response;
    }

    @Transactional
    public void deleteMyPet(Long petId, User user) {
        log.info("애완동물 삭제 : {}", petId);
        Pet pet = petRepository.findByIdAndUserId(petId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("존재하지 않는 애완동물 : %s", petId)));
        pet.delete();
        log.info("애완동물 삭제 성공 : {}", petId);
    }

    @Transactional
    public MyPetUpdateResponse updateMyPet(MyPetUpdateRequest request, Long petId, User user) {
        log.info("애완동물 정보 변경 : {}", petId);
        Pet pet = petRepository.findByIdAndUserId(petId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("존재하지 않는 펫 ID : %d", petId)));
        pet.updatePetInfo(request.getName(), request.getBreed(), request.getAge(), request.isNeutered(), request.getWeight(), request.getDescription());

        List<Image> deleteImages = s3Service.deleteImageById(request.getDeleteImageIds());
        pet.deleteImage(deleteImages);

        List<Image> images = s3Service.uploadImage(request.getImages());
        pet.addImage(images);

        MyPetUpdateResponse response = new MyPetUpdateResponse();
        BeanUtils.copyProperties(pet, response);

        response.setPetType(buildPetPropertySimpleDto(pet.getPetType()));
        response.setImages(buildImageDtoList(pet.getImages()));

        log.info("애완동물 정보 변경 성공 : {}", petId);
        return response;
    }

    public MyPetViewResponse getMyPet(Long petId) {
        log.info("애완동물 조회 : {}", petId);
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("애완동물이 존재하지 않음 : %s", petId)));

        MyPetViewResponse response = new MyPetViewResponse();
        BeanUtils.copyProperties(pet, response);

        response.setPetType(buildPetPropertySimpleDto(pet.getPetType()));
        response.setImages(buildImageDtoList(pet.getImages()));

        log.info("애완동물 조회 성공 : {}", pet.getId());
        return response;
    }

    private static List<ImageSimpleDto> buildImageDtoList(List<PetImage> images) {
        return images.stream()
                .map(PetImage::getImage)
                .map(image -> new ImageSimpleDto(image.getId(), image.getName()))
                .toList();
    }

    @NotNull
    private static PetPropertySimpleDto buildPetPropertySimpleDto(PetProperty pet) {
        return new PetPropertySimpleDto(pet.getId(), pet.getName(), pet.getOrder());
    }
}
