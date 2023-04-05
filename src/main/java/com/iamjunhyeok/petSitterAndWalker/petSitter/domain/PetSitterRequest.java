package com.iamjunhyeok.petSitterAndWalker.petSitter.domain;

import com.iamjunhyeok.petSitterAndWalker.common.domain.DateTime;
import com.iamjunhyeok.petSitterAndWalker.constants.enums.RequestStatus;
import com.iamjunhyeok.petSitterAndWalker.pet.domain.Pet;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(length = 200)
    private String message;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(length = 100)
    private String rejectReason;

    @Builder.Default
    @OneToMany(mappedBy = "petSitterRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetSitterRequestPet> pets = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "petSitterRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetSitterRequestOption> options = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_sitter_id")
    private PetSitter petSitter;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "pet_sitter_review_id", unique = true)
    private PetSitterReview review;

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

    public void accept() {
        checkRequestStatus();
        this.status = RequestStatus.ACCEPTED;
    }

    public void reject() {
        checkRequestStatus();
        this.status = RequestStatus.REJECTED;
    }

    public void cancel() {
        checkRequestStatus();
        this.status = RequestStatus.CANCELED;
    }

    private void checkRequestStatus() {
        if (this.status != RequestStatus.REQUESTED) {
            throw new IllegalStateException(String.format("해당 요청 정보는 'REQUESTED' 상태가 아님 : %s", this.getId()));
        }
    }

    public void registerReview(PetSitterReview petSitterReview) {
        if (!status.equals(RequestStatus.ACCEPTED) || endDate.isAfter(LocalDateTime.now())) {
            throw new IllegalStateException(String.format("리뷰를 남길 수 없는 상태 : %s", id));
        }
        this.review = petSitterReview;
    }
}
