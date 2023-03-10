package com.iamjunhyeok.petSitterAndWalker.domain;

import com.iamjunhyeok.petSitterAndWalker.domain.common.DateTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false)
    private String intro;

    private int experience;

    private boolean isAvailable;

    private int views;

    private int reviews;

    private float averageRating;

    @OneToMany(mappedBy = "petSitter")
    private List<PetSitterPetProperty> petTypes = new ArrayList<>();

    @OneToMany(mappedBy = "petSitter")
    private List<PetSitterPetProperty> petSizes = new ArrayList<>();

    @OneToMany(mappedBy = "petSitter", cascade = CascadeType.ALL)
    private List<PetSitterOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "petSitter", cascade = CascadeType.ALL)
    private List<PetSitterImage> images = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public PetSitter(String intro, int experience) {
        this.intro = intro;
        this.experience = experience;
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
        PetSitterPetProperty petSitterPetProperty = new PetSitterPetProperty(this, petProperty);
        petTypes.add(petSitterPetProperty);
    }

    public void addPetType(List<PetProperty> petProperties) {
        for (PetProperty petProperty : petProperties) {
            addPetType(petProperty);
        }
    }

    public void addPetSize(PetProperty petProperty) {
        PetSitterPetProperty petSitterPetProperty = new PetSitterPetProperty(this, petProperty);
        petSizes.add(petSitterPetProperty);
    }

    public void addPetSize(List<PetProperty> petProperties) {
        for (PetProperty petProperty : petProperties) {
            addPetType(petProperty);
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

    public void setUser(User user) {
        this.user = user;
    }
}
