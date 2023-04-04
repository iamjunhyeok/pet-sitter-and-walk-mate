package com.iamjunhyeok.petSitterAndWalker.image.domain;

import com.iamjunhyeok.petSitterAndWalker.common.domain.DateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Image extends DateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "image_id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    public Image(String name) {
        this.name = name;
    }
}
