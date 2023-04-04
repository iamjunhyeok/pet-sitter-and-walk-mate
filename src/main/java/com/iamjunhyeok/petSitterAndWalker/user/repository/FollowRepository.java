package com.iamjunhyeok.petSitterAndWalker.user.repository;

import com.iamjunhyeok.petSitterAndWalker.user.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
}
