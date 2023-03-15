package com.iamjunhyeok.petSitterAndWalker.repository;

import com.iamjunhyeok.petSitterAndWalker.domain.PetSitter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PetSitterRepository extends JpaRepository<PetSitter, Long> {

    @Query("select ps from PetSitter ps join ps.user where ps.user.id = :userId")
    Optional<PetSitter> findByUserId(Long userId);
}
