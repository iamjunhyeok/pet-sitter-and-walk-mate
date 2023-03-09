package com.iamjunhyeok.petSitterAndWalker.initializer;

import com.iamjunhyeok.petSitterAndWalker.domain.PetType;
import com.iamjunhyeok.petSitterAndWalker.repository.PetTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PetTypeInitializer implements CommandLineRunner {

    private final PetTypeRepository petTypeRepository;

    @Override
    public void run(String... args) throws Exception {
        List<PetType> petTypes = Arrays.asList(
                new PetType("강아지"),
                new PetType("고양이")
        );
        petTypeRepository.saveAll(petTypes);
    }
}
