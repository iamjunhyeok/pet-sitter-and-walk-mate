package com.iamjunhyeok.petSitterAndWalker.controller;

import com.iamjunhyeok.petSitterAndWalker.dto.VerificationRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.VerifyRequest;
import com.iamjunhyeok.petSitterAndWalker.service.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class VerificationController {

    private final VerificationService verificationService;

    @Value("${coolsms.from}")
    private String from;

    @PostMapping("/send-verification-code")
    public SingleMessageSentResponse sendVerificationCode(HttpServletRequest httpServletRequest, @RequestBody @Valid VerificationRequest request) {
        return verificationService.sendVerificationCode(httpServletRequest, request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/verify")
    public void verify(@RequestBody @Valid VerifyRequest request) {
        verificationService.verify(request);
    }
}
