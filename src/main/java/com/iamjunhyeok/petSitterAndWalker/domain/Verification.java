package com.iamjunhyeok.petSitterAndWalker.domain;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.VerificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Entity
public class Verification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "verification_id", nullable = false)
    private Long id;

    private String phoneNumber;

    private String verificationCode;

    private String ipAddress;

    @Enumerated(EnumType.STRING)
    private VerificationStatus status;

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    public Verification(String phoneNumber, String verificationCode, String ipAddress) {
        this.phoneNumber = phoneNumber;
        this.verificationCode = verificationCode;
        this.ipAddress = ipAddress;
    }

    public void changeStatus(VerificationStatus status) {
        this.status = status;
    }
}
