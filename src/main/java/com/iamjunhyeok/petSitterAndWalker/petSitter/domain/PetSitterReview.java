package com.iamjunhyeok.petSitterAndWalker.petSitter.domain;

import com.iamjunhyeok.petSitterAndWalker.common.domain.DateTime;
import com.iamjunhyeok.petSitterAndWalker.image.domain.Image;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PetSitterReview extends DateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pet_sitter_review_id", nullable = false)
    private Long id;

    private int rating;

    @Column(length = 200)
    private String comment;

    private boolean isDeleted;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_sitter_request_id", nullable = false, unique = true)
    private PetSitterRequest request;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetSitterReviewImage> images = new ArrayList<>();

    public PetSitterReview(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public PetSitterReview(int rating, String comment, List<PetSitterReviewImage> images) {
        this.rating = rating;
        this.comment = comment;
        this.images = images;
    }

    public void update(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void addImage(Image image) {
        PetSitterReviewImage petSitterReviewImage = new PetSitterReviewImage(this, image);
        this.images.add(petSitterReviewImage);
    }

    public void addImages(List<Image> images) {
        for (Image image : images) {
            addImage(image);
        }
    }

    public void deleteImage(Image image) {
        this.images.removeIf(petSitterReviewImage -> petSitterReviewImage.getImage() == image);
    }

    public void deleteImages(List<Image> deletedImages) {
        for (Image deletedImage : deletedImages) {
            deleteImage(deletedImage);
        }
    }

    public void setRequest(PetSitterRequest petSitterRequest) {
        this.request = petSitterRequest;
    }
}
