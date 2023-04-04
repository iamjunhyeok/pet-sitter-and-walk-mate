package com.iamjunhyeok.petSitterAndWalker.pet.repository;

import com.iamjunhyeok.petSitterAndWalker.pet.domain.PetProperty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetPropertyRepository extends JpaRepository<PetProperty, Long> {
}
