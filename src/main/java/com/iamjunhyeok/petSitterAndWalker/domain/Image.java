package com.iamjunhyeok.petSitterAndWalker.domain;

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
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "image_id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public Image(String name) {
        this.name = name;
    }
}
