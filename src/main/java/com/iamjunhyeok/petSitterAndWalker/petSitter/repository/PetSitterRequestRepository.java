package com.iamjunhyeok.petSitterAndWalker.petSitter.repository;

import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetSitterRequestRepository extends JpaRepository<PetSitterRequest, Long> {

    Optional<PetSitterRequest> findByIdAndPetSitterId(Long requestId, Long petSitterId);

}