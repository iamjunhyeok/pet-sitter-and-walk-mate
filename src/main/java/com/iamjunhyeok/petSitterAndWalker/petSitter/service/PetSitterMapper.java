package com.iamjunhyeok.petSitterAndWalker.petSitter.service;

import com.iamjunhyeok.petSitterAndWalker.image.dto.ImageSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.pet.dto.PetPropertySimpleDto;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterImage;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterOption;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterPetSize;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterPetType;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterOptionSimpleDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PetSitterMapper {

    public List<PetPropertySimpleDto> buildPetTypeDtoList(List<PetSitterPetType> petTypes) {
        return petTypes.stream()
                .map(PetSitterPetType::getPetProperty)
                .map(petProperty -> new PetPropertySimpleDto(petProperty.getId(), petProperty.getName(), petProperty.getOrder()))
                .toList();
    }

    public List<PetPropertySimpleDto> buildPetSizeDtoList(List<PetSitterPetSize> petSizes) {
        return petSizes.stream()
                .map(PetSitterPetSize::getPetProperty)
                .map(petProperty -> new PetPropertySimpleDto(petProperty.getId(), petProperty.getName(), petProperty.getOrder()))
                .toList();
    }

    public List<PetSitterOptionSimpleDto> buildPetSitterOptionDtoList(List<PetSitterOption> options) {
        return options.stream()
                .map(petSitterOption -> new PetSitterOptionSimpleDto(petSitterOption.getId(), petSitterOption.getName(), petSitterOption.getPrice()))
                .toList();
    }

    public List<ImageSimpleDto> buildImageDtoList(List<PetSitterImage> images) {
        return images.stream()
                .map(PetSitterImage::getImage)
                .map(image -> new ImageSimpleDto(image.getId(), image.getName()))
                .toList();
    }
}
