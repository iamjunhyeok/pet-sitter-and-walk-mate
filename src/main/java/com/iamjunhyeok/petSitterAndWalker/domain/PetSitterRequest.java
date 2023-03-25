package com.iamjunhyeok.petSitterAndWalker.domain;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.RequestStatus;
import com.iamjunhyeok.petSitterAndWalker.domain.common.DateTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
public class PetSitterRequest extends DateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pet_sitter_request_id", nullable = false)
    private Long id;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String message;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private String rejectReason;

    @Builder.Default
    @OneToMany(mappedBy = "petSitterRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetSitterRequestPet> pets = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "petSitterRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetSitterRequestOption> options = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void addPet(Pet pet) {
        PetSitterRequestPet petSitterRequestPet = new PetSitterRequestPet(this, pet);
        this.pets.add(petSitterRequestPet);
    }

    public void addPet(List<Pet> pets) {
        for (Pet pet : pets) {
            addPet(pet);
        }
    }

    public void addOption(PetSitterOption option) {
        PetSitterRequestOption petSitterRequestOption = new PetSitterRequestOption(this, option);
        this.options.add(petSitterRequestOption);
    }

    public void addOption(List<PetSitterOption> options) {
        for (PetSitterOption option : options) {
            addOption(option);
        }
    }
}
