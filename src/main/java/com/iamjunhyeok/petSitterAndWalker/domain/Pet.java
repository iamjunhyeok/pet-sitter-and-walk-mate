package com.iamjunhyeok.petSitterAndWalker.domain;

import com.iamjunhyeok.petSitterAndWalker.constants.Gender;
import com.iamjunhyeok.petSitterAndWalker.dto.common.DateTime;
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
    private String intro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL)
    private List<PetImage> images = new ArrayList<>();

    public void setOwner(User user) {
        this.user = user;
    }

    public void addImage(Image image) {
        if (images == null) images = new ArrayList<>();
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
}
