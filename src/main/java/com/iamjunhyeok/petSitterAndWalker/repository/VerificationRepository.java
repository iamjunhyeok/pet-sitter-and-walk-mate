package com.iamjunhyeok.petSitterAndWalker.repository;

import com.iamjunhyeok.petSitterAndWalker.domain.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationRepository extends JpaRepository<Verification, Long> {

    @Query("select count(v) from Verification v where cast(v.createdDate as localdate) = :today and (v.phoneNumber = :phoneNumber or v.ipAddress = :ipAddress)")
    long countByPhoneNumberOrIpAddressToday(String phoneNumber, String ipAddress, LocalDate today);

    Optional<Verification> findFirstByPhoneNumberAndCreatedDateGreaterThanOrderByCreatedDateDesc(String phoneNumber, LocalDateTime from);
}
