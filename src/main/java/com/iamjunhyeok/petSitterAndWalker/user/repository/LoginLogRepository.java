package com.iamjunhyeok.petSitterAndWalker.user.repository;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.LoginStatus;
import com.iamjunhyeok.petSitterAndWalker.user.domain.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {

    Optional<LoginLog> findByEmailAndStatus(String email, LoginStatus status);
}