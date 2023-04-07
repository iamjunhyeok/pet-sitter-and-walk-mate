package com.iamjunhyeok.petSitterAndWalker.petSitter.repository;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.RequestStatus;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitter;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterReview;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class PetSitterReviewRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PetSitterReviewRepository petSitterReviewRepository;

    @Test
    @DisplayName("펫 시터에 대한 모든 리뷰 조회")
    void testFindAllPetSitterId() {
        // Arrange
        PetSitter petSitter = new PetSitter("I'm pet sitter");
        testEntityManager.persist(petSitter);

        PetSitterRequest request = PetSitterRequest.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .status(RequestStatus.ACCEPTED)
                .petSitter(petSitter)
                .build();
        testEntityManager.persist(request);

        PetSitterReview review = new PetSitterReview(3, "SoSo~");
        review.setRequest(request);
        testEntityManager.persist(review);

        // Act
        Page<PetSitterReview> result = petSitterReviewRepository.findAllByPetSitterId(petSitter.getId(), PageRequest.of(0, 10));

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals(review.getRating(), result.getContent().get(0).getRating());
        assertEquals(review.getComment(), result.getContent().get(0).getComment());
    }

    @Test
    @DisplayName("리뷰가 펫 시터에 대한 리뷰인지 확인")
    void testFindByIdAndPetSitterId() {
        // Arrange
        PetSitter petSitter = new PetSitter("I'm pet sitter");
        testEntityManager.persist(petSitter);

        PetSitterRequest request = PetSitterRequest.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .status(RequestStatus.ACCEPTED)
                .petSitter(petSitter)
                .build();
        testEntityManager.persist(request);

        PetSitterReview review = new PetSitterReview(3, "SoSo~");
        review.setRequest(request);
        testEntityManager.persist(review);

        // Act
        PetSitterReview result = petSitterReviewRepository.findByIdAndPetSitterId(review.getId(), petSitter.getId()).get();

        // Assert
        assertEquals(review.getRating(), result.getRating());
        assertEquals(review.getComment(), result.getComment());
    }
}