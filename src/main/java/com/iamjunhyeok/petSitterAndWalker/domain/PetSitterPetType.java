package com.iamjunhyeok.petSitterAndWalker.domain;

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
public class PetSitterPetType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pet_sitter_pet_type_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pet_sitter_id")
    private PetSitter petSitter;

    @ManyToOne
    @JoinColumn(name = "pet_type_id")
    private PetProperty petProperty;

    public PetSitterPetType(PetSitter petSitter, PetProperty petProperty) {
        this.petSitter = petSitter;
        this.petProperty = petProperty;
    }
}
