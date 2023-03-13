package com.iamjunhyeok.petSitterAndWalker.domain;

import com.iamjunhyeok.petSitterAndWalker.domain.common.DateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "users")
public class User extends DateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, length = 11)
    private String phoneNumber;

    @Column(nullable = false, length = 10)
    private String zipCode;

    @Column(nullable = false, length = 20)
    private String address1;

    @Column(nullable = false, length = 20)
    private String address2;

    private int followers;

    private boolean isPetSitter;

    private boolean isWalkMate;

    private boolean isDeleted;

    private LocalDateTime lastAccessDate;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Pet> pets = new ArrayList<>();

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private PetSitter petSitter;

    public void registerPet(Pet pet) {
        pets.add(pet);
        pet.setOwner(this);
    }

    public void registerPetSitterInfo(PetSitter petSitter) {
        this.petSitter = petSitter;
        petSitter.setUser(this);
    }

    public void updateUserInfo(String name, String phoneNumber, String zipCode, String address1, String address2) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.zipCode = zipCode;
        this.address1 = address1;
        this.address2 = address2;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
}
