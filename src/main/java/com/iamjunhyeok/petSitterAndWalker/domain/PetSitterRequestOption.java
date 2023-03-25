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
public class PetSitterRequestOption {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pet_sitter_request_option_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pet_sitter_request_id")
    private PetSitterRequest petSitterRequest;

    @ManyToOne
    @JoinColumn(name = "pet_sitter_option_id")
    private PetSitterOption petSitterOption;

    public PetSitterRequestOption(PetSitterRequest petSitterRequest, PetSitterOption petSitterOption) {
        this.petSitterRequest = petSitterRequest;
        this.petSitterOption = petSitterOption;
    }
}
