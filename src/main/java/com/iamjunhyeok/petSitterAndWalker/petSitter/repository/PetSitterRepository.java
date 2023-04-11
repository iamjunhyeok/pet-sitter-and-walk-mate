package com.iamjunhyeok.petSitterAndWalker.petSitter.repository;

import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PetSitterRepository extends JpaRepository<PetSitter, Long> {

    @Query("select ps from PetSitter ps join ps.user where ps.user.id = :userId")
    Optional<PetSitter> findByUserId(Long userId);

    @Query(value = "select ps from PetSitter ps join fetch ps.user u left join fetch ps.images i",
            countQuery = "select count(ps) from PetSitter ps")
    Page<PetSitter> findAll(Pageable pageable);

    @Query("select ps from PetSitter ps join fetch ps.user")
    Optional<PetSitter> findById(Long id);
}
