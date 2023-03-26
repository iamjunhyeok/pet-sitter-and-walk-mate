package com.iamjunhyeok.petSitterAndWalker.repository;

import com.iamjunhyeok.petSitterAndWalker.domain.PetSitterRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetSitterRequestRepository extends JpaRepository<PetSitterRequest, Long> {

    Optional<PetSitterRequest> findByIdAndPetSitterId(Long requestId, Long petSitterId);

}