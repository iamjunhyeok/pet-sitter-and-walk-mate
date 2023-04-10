package com.iamjunhyeok.petSitterAndWalker.user.domain;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.LoginStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class LoginLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "login_log_id", nullable = false)
    private Long id;

    private String email;

    private LocalDateTime loginDate;

    private String ipAddress;

    private String userAgent;

    @Enumerated(EnumType.STRING)
    private LoginStatus status;

    private String message;

    public LoginLog(String email, String ipAddress, String userAgent) {
        this.email = email;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.loginDate = LocalDateTime.now();
        this.status = LoginStatus.PENDING;
    }

    public void changeStatus(LoginStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
