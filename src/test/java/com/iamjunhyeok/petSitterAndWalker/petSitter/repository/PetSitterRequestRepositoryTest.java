package com.iamjunhyeok.petSitterAndWalker.petSitter.repository;

import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitter;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class PetSitterRequestRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PetSitterRequestRepository petSitterRequestRepository;

    @Test
    @DisplayName("펫 시터에 대한 요청 조회")
    void testFindByIdAndPetSitterId() {
        // Arrange
        PetSitter petSitter = new PetSitter("hello");
        testEntityManager.persist(petSitter);

        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .petSitter(petSitter)
                .build();
        testEntityManager.persist(petSitterRequest);

        // Act
        PetSitterRequest findPetSitterRequest = petSitterRequestRepository.findByIdAndPetSitterId(petSitterRequest.getId(), petSitter.getId()).get();

        // Assert
        assertEquals(petSitter.getId(), findPetSitterRequest.getPetSitter().getId());
        assertEquals(petSitterRequest.getId(), findPetSitterRequest.getId());
    }
}