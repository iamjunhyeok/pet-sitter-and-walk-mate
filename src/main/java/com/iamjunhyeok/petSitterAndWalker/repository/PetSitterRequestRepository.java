package com.iamjunhyeok.petSitterAndWalker.repository;

import com.iamjunhyeok.petSitterAndWalker.domain.PetSitterRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetSitterRequestRepository extends JpaRepository<PetSitterRequest, Long> {
}