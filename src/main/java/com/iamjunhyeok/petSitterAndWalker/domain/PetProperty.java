package com.iamjunhyeok.petSitterAndWalker.domain;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.PetPropertyEnum;
import com.iamjunhyeok.petSitterAndWalker.domain.common.DateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class PetProperty extends DateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pet_property_id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PetPropertyEnum property;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(name = "orders")
    private int order;

    public PetProperty(PetPropertyEnum property, String name) {
        this.property = property;
        this.name = name;
    }
}
