package com.iamjunhyeok.petSitterAndWalker.domain;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.VerificationStatus;
import com.iamjunhyeok.petSitterAndWalker.domain.common.DateTime;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Entity
public class Verification extends DateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "verification_id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 11)
    private String phoneNumber;

    @Column(nullable = false, length = 6)
    private String verificationCode;

    @Column(nullable = false, length = 45)
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    private VerificationStatus status;

    public Verification(String phoneNumber, String verificationCode, String ipAddress) {
        this.phoneNumber = phoneNumber;
        this.verificationCode = verificationCode;
        this.ipAddress = ipAddress;
    }

    public void changeStatus(VerificationStatus status) {
        this.status = status;
    }
}
