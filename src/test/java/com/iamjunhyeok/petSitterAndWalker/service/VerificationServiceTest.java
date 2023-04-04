package com.iamjunhyeok.petSitterAndWalker.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VerificationServiceTest {

    private VerificationService verificationService;

    @RepeatedTest(100)
    @DisplayName("인증번호 생성")
    void testWhenGenerateRandom() {
        // Arrange

        // Act
        String s = verificationService.generateVerificationCode();

        // Assert
        assertEquals(6, s.length());
    }
}