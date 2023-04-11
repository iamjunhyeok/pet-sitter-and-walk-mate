package com.iamjunhyeok.petSitterAndWalker.pet.repository;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.PetPropertyEnum;
import com.iamjunhyeok.petSitterAndWalker.pet.domain.Pet;
import com.iamjunhyeok.petSitterAndWalker.pet.domain.PetProperty;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class PetRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PetRepository petRepository;

    @Test
    @DisplayName("사용자 애완동물 조회")
    void testFindByUserId() {
        // Arrange
        PetProperty petType = new PetProperty(PetPropertyEnum.TYPE, "강아지");
        testEntityManager.persist(petType);

        Pet pet1 = Pet.builder()
                .name("애기")
                .breed("말티즈")
                .petType(petType)
                .build();
        testEntityManager.persist(pet1);

        Pet pet2 = Pet.builder()
                .name("후추")
                .breed("포메라니안")
                .petType(petType)
                .build();
        testEntityManager.persist(pet2);

        User user = User.builder()
                .name("사용자")
                .email("jeonjhyeok@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기 성남시")
                .address2("1층")
                .build();
        user.addPet(pet1);
        user.addPet(pet2);
        testEntityManager.persist(user);

        // Act
        List<Pet> pets = petRepository.findByUserId(user.getId());

        // Assert
        assertEquals(user.getPets().size(), pets.size());
    }

    @Test
    @DisplayName("사용자의 애완동물 조회")
    void testFindByIdAndUserId() {
        // Arrange
        PetProperty petType = new PetProperty(PetPropertyEnum.TYPE, "강아지");
        testEntityManager.persist(petType);

        Pet pet1 = Pet.builder()
                .name("애기")
                .breed("말티즈")
                .petType(petType)
                .build();
        testEntityManager.persist(pet1);

        Pet pet2 = Pet.builder()
                .name("후추")
                .breed("포메라니안")
                .petType(petType)
                .build();
        testEntityManager.persist(pet2);

        User user = User.builder()
                .name("사용자")
                .email("jeonjhyeok@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기 성남시")
                .address2("1층")
                .build();
        user.addPet(pet1);
        user.addPet(pet2);
        testEntityManager.persist(user);

        // Act
        Pet findPet = petRepository.findByIdAndUserId(pet1.getId(), user.getId()).get();

        // Assert
        assertEquals(user.getPets().get(0).getName(), findPet.getName());
    }
}