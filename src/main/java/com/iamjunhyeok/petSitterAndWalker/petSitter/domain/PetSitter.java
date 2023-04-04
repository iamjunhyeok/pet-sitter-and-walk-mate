package com.iamjunhyeok.petSitterAndWalker.petSitter.domain;

import com.iamjunhyeok.petSitterAndWalker.image.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.common.domain.DateTime;
import com.iamjunhyeok.petSitterAndWalker.pet.domain.PetProperty;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class PetSitter extends DateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pet_sitter_id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 200)
    private String introduction;

    private boolean isAvailable;

    private int views;

    private int reviews;

    private float averageRating;

    @OneToMany(mappedBy = "petSitter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetSitterPetType> petTypes = new ArrayList<>();

    @OneToMany(mappedBy = "petSitter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetSitterPetSize> petSizes = new ArrayList<>();

    @Where(clause = "is_deleted=false")
    @OneToMany(mappedBy = "petSitter", cascade = CascadeType.ALL)
    private List<PetSitterOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "petSitter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetSitterImage> images = new ArrayList<>();

    @OneToOne(mappedBy = "petSitter")
    private User user;

    public PetSitter(String introduction) {
        this.introduction = introduction;
    }

    public void addOption(PetSitterOption option) {
        options.add(option);
        option.setPetSitter(this);
    }

    public void addOption(List<PetSitterOption> options) {
        for (PetSitterOption option : options) {
            addOption(option);
        }
    }

    public void addPetType(PetProperty petProperty) {
        PetSitterPetType petSitterPetType = new PetSitterPetType(this, petProperty);
        petTypes.add(petSitterPetType);
    }

    public void addPetType(List<PetProperty> petProperties) {
        for (PetProperty petProperty : petProperties) {
            addPetType(petProperty);
        }
    }

    public void addPetSize(PetProperty petProperty) {
        PetSitterPetSize petSitterPetSize = new PetSitterPetSize(this, petProperty);
        petSizes.add(petSitterPetSize);
    }

    public void addPetSize(List<PetProperty> petProperties) {
        for (PetProperty petProperty : petProperties) {
            addPetSize(petProperty);
        }
    }

    public void addImage(Image image) {
        PetSitterImage petSitterImage = new PetSitterImage(this, image);
        this.images.add(petSitterImage);
    }

    public void addImage(List<Image> images) {
        for (Image image : images) {
            addImage(image);
        }
    }

    public void deleteImage(Image image) {
        this.images.removeIf(petSitterImage -> petSitterImage.getImage() == image);
    }

    public void deleteImage(List<Image> images) {
        if (images == null || images.isEmpty()) return;
        for (Image image : images) {
            deleteImage(image);
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void changeIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void deleteAllOptions() {
        for (PetSitterOption option : options) {
            option.delete();
        }
    }

    public void clearAndAddPetTypes(List<PetProperty> petTypes) {
        this.petTypes.clear();
        addPetType(petTypes);
    }

    public void clearAndAddPetSizes(List<PetProperty> petSizes) {
        this.petSizes.clear();
        addPetSize(petSizes);
    }
}
