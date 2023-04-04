package com.iamjunhyeok.petSitterAndWalker.petSitter.domain;

import com.iamjunhyeok.petSitterAndWalker.pet.domain.PetProperty;
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
public class PetSitterPetSize {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pet_sitter_pet_type_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pet_sitter_id")
    private PetSitter petSitter;

    @ManyToOne
    @JoinColumn(name = "pet_size_id")
    private PetProperty petProperty;

    public PetSitterPetSize(PetSitter petSitter, PetProperty petProperty) {
        this.petSitter = petSitter;
        this.petProperty = petProperty;
    }
}
