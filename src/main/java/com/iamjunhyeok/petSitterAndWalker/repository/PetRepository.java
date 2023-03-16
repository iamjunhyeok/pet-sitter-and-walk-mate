package com.iamjunhyeok.petSitterAndWalker.repository;

import com.iamjunhyeok.petSitterAndWalker.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

    @Query("select p from Pet p join p.user u join fetch p.petType pt join fetch p.images pi where u.id = :userId order by pi.order")
    List<Pet> findByUserId(Long userId);

    @Override
    @Modifying
    @Query("update Pet p set p.isDeleted = true, p.lastModifiedDate = current_timestamp where p.id = :petId")
    void deleteById(Long petId);
}