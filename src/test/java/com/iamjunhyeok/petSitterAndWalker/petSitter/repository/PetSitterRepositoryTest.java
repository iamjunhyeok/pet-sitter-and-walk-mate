package com.iamjunhyeok.petSitterAndWalker.petSitter.repository;

import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitter;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class PetSitterRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PetSitterRepository petSitterRepository;

    @Test
    @DisplayName("사용자의 펫 시터 정보 조회")
    void testFindByUserId() {
        // Arrange
        PetSitter petSitter = new PetSitter("hello");
        testEntityManager.persist(petSitter);

        User user = User.builder()
                .name("사용자")
                .email("jeonjhyeok@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기 성남시")
                .address2("1층")
                .build();
        user.registerPetSitterInfo(petSitter);
        testEntityManager.persist(user);

        // Act
        PetSitter findPetSitter = petSitterRepository.findByUserId(user.getId()).get();

        // Assert
        assertEquals(petSitter.getIntroduction(), findPetSitter.getIntroduction());
        assertEquals(petSitter.getUser().getId(), user.getId());
        assertEquals(petSitter.getUser().getName(), user.getName());
        assertEquals(petSitter.getUser().getEmail(), user.getEmail());
    }

    @Test
    @DisplayName("펫 시터 목록 조회")
    void testFindAll() {
        // Arrange
        PetSitter petSitter1 = new PetSitter("hello1");
        testEntityManager.persist(petSitter1);

        PetSitter petSitter2 = new PetSitter("hello2");
        testEntityManager.persist(petSitter2);

        User user1 = User.builder()
                .name("펫시터1")
                .email("petSitter1@gmail.com")
                .password("123456")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("서울시")
                .address2("1층")
                .build();
        user1.registerPetSitterInfo(petSitter1);
        testEntityManager.persist(user1);

        User user2 = User.builder()
                .name("펫시터2")
                .email("petSitter2@gmail.com")
                .password("123456")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("서울시")
                .address2("1층")
                .build();
        user2.registerPetSitterInfo(petSitter2);
        testEntityManager.persist(user2);

        // Act
        Page<PetSitter> findPetSitters = petSitterRepository.findAll(PageRequest.of(0, 10));

        // Assert
        assertEquals(2, findPetSitters.getContent().size());
        assertEquals(petSitter1.getId(), findPetSitters.getContent().get(0).getId());
        assertEquals(petSitter2.getId(), findPetSitters.getContent().get(1).getId());
    }

    @Test
    @DisplayName("펫 시터 정보 조회")
    void testFindById() {
        // Arrange
        PetSitter petSitter = new PetSitter("hello");
        testEntityManager.persist(petSitter);

        User user = User.builder()
                .name("펫시터1")
                .email("petSitter1@gmail.com")
                .password("123456")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("서울시")
                .address2("1층")
                .build();
        user.registerPetSitterInfo(petSitter);
        testEntityManager.persist(user);

        // Act
        PetSitter findPetSitter = petSitterRepository.findById(petSitter.getId()).get();

        // Assert
        assertEquals(petSitter.getId(), findPetSitter.getId());
        assertEquals(petSitter.getIntroduction(), findPetSitter.getIntroduction());
        assertEquals(petSitter.getUser().getId(), findPetSitter.getUser().getId());
    }
}