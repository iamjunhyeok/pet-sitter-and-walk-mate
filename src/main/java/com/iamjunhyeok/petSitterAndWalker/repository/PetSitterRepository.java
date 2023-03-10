package com.iamjunhyeok.petSitterAndWalker.repository;

import com.iamjunhyeok.petSitterAndWalker.domain.PetSitter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetSitterRepository extends JpaRepository<PetSitter, Long> {

}
