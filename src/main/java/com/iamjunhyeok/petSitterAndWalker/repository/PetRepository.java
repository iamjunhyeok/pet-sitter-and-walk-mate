package com.iamjunhyeok.petSitterAndWalker.repository;

import com.iamjunhyeok.petSitterAndWalker.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

    @Query("select p from Pet p join fetch p.user u join fetch p.images pi where u.id = :userId order by pi.order")
    List<Pet> findByUserId(Long userId);
}