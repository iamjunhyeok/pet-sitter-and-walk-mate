package com.iamjunhyeok.petSitterAndWalker.pet.domain;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.Gender;
import com.iamjunhyeok.petSitterAndWalker.image.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.common.domain.DateTime;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
public class Pet extends DateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pet_id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 20)
    private String breed;

    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private boolean isNeutered;

    private int weight;

    @Column(length = 200)
    private String description;

    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL)
    private List<PetImage> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_property_id")
    private PetProperty petType;

    public void setOwner(User user) {
        this.user = user;
    }

    public void addImage(Image image) {
        PetImage petImage = new PetImage(this, image, images.size());
        images.add(petImage);
    }

    public void addImage(List<Image> images) {
        for (Image image : images) {
            addImage(image);
        }
    }

    public Image getRepresentativeImage() {
        return !images.isEmpty() ? images.get(0).getImage() : null;
    }

    public void deleteImage(Image image) {
        this.images.removeIf(petImage -> petImage.getImage() == image);
    }

    public void deleteImage(List<Image> images) {
        if (images == null || images.isEmpty()) return;
        for (Image image : images) {
            deleteImage(image);
        }
    }

    public void updatePetInfo(String name, String breed, int age, boolean neutered, int weight, String description) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.isNeutered = neutered;
        this.weight = weight;
        this.description = description;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
