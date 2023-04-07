package com.iamjunhyeok.petSitterAndWalker.petSitter.repository;

import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PetSitterReviewRepository extends JpaRepository<PetSitterReview, Long> {

    @Query(value = "select psr from PetSitterReview psr join fetch psr.request r left outer join fetch psr.images i where r.petSitter.id = :petSitterId",
            countQuery = "select count(psr) from PetSitterReview psr")
    Page<PetSitterReview> findAllByPetSitterId(Long petSitterId, Pageable pageable);

    @Query("select psr from PetSitterReview psr join fetch psr.request r join fetch r.petSitter ps where psr.id = :reviewId and ps.id = :petSitterId")
    Optional<PetSitterReview> findByIdAndPetSitterId(Long reviewId, Long petSitterId);

}
