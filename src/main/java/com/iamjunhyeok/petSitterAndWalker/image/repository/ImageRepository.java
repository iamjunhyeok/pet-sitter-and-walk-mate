package com.iamjunhyeok.petSitterAndWalker.image.repository;

import com.iamjunhyeok.petSitterAndWalker.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
