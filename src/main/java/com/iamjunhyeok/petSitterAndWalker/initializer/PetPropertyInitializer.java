package com.iamjunhyeok.petSitterAndWalker.initializer;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.PetPropertyEnum;
import com.iamjunhyeok.petSitterAndWalker.pet.domain.PetProperty;
import com.iamjunhyeok.petSitterAndWalker.pet.repository.PetPropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PetPropertyInitializer implements CommandLineRunner {

    private final PetPropertyRepository petPropertyRepository;

    @Override
    public void run(String... args) throws Exception {
        List<PetProperty> petProperties = Arrays.asList(
                new PetProperty(PetPropertyEnum.TYPE, "강아지"),
                new PetProperty(PetPropertyEnum.TYPE, "고양이"),
                new PetProperty(PetPropertyEnum.TYPE, "파충류"),
                new PetProperty(PetPropertyEnum.TYPE, "물고기"),
                new PetProperty(PetPropertyEnum.TYPE, "조류"),
                new PetProperty(PetPropertyEnum.TYPE, "고슴도치"),
                new PetProperty(PetPropertyEnum.SIZE, "작은 0~7kg"),
                new PetProperty(PetPropertyEnum.SIZE, "중간 7~18kg"),
                new PetProperty(PetPropertyEnum.SIZE, "큰 19~45kg"),
                new PetProperty(PetPropertyEnum.SIZE, "거대 45+kg")
        );
        petPropertyRepository.saveAll(petProperties);
    }
}
