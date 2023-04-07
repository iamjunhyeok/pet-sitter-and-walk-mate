package com.iamjunhyeok.petSitterAndWalker.petSitter.domain;

import com.iamjunhyeok.petSitterAndWalker.image.domain.Image;
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
public class PetSitterReviewImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pet_sitter_review_image_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pet_sitter_review_id")
    private PetSitterReview review;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image image;

    public PetSitterReviewImage(PetSitterReview review, Image image) {
        this.review = review;
        this.image = image;
    }
}
