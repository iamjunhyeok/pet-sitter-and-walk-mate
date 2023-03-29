package com.iamjunhyeok.petSitterAndWalker.controller;

import com.iamjunhyeok.petSitterAndWalker.dto.VerifyRequest;
import com.iamjunhyeok.petSitterAndWalker.service.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/verification")
@RestController
public class VerificationController {

    private final VerificationService verificationService;

    @Value("${coolsms.from}")
    private String from;

    @PostMapping("/send")
    public SingleMessageSentResponse send(HttpServletRequest httpServletRequest,
                                          @RequestParam @Size(min = 11, max = 11) String phoneNumber) {
        return verificationService.send(httpServletRequest, phoneNumber);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/verify")
    public void verify(@RequestBody @Valid VerifyRequest request) {
        verificationService.verify(request);
    }
}
