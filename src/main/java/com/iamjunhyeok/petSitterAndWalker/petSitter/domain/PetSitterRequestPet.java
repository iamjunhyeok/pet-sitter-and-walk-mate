package com.iamjunhyeok.petSitterAndWalker.petSitter.domain;

import com.iamjunhyeok.petSitterAndWalker.pet.domain.Pet;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class PetSitterRequestPet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pet_sitter_request_pet_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pet_sitter_request_id")
    private PetSitterRequest petSitterRequest;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    public PetSitterRequestPet(PetSitterRequest petSitterRequest, Pet pet) {
        this.petSitterRequest = petSitterRequest;
        this.pet = pet;
    }
}
