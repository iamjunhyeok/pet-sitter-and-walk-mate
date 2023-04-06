package com.iamjunhyeok.petSitterAndWalker.petSitter.domain;

import com.iamjunhyeok.petSitterAndWalker.common.domain.DateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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

    @OneToOne(mappedBy = "review")
    private PetSitterRequest request;

    public PetSitterReview(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public void update(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
