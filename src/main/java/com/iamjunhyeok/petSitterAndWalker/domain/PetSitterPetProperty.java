package com.iamjunhyeok.petSitterAndWalker.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class PetSitterPetProperty {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pet_sitter_pet_property_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_sitter_id")
    private PetSitter petSitter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_property_id")
    private PetProperty petProperty;

    public PetSitterPetProperty(PetSitter petSitter, PetProperty petProperty) {
        this.petSitter = petSitter;
        this.petProperty = petProperty;
    }
}
