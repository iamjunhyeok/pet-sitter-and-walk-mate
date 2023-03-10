package com.iamjunhyeok.petSitterAndWalker.repository;

import com.iamjunhyeok.petSitterAndWalker.domain.PetProperty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetPropertyRepository extends JpaRepository<PetProperty, Long> {
}
