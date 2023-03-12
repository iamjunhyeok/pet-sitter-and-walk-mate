package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.VerificationStatus;
import com.iamjunhyeok.petSitterAndWalker.domain.Verification;
import com.iamjunhyeok.petSitterAndWalker.dto.VerificationRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.VerifyRequest;
import com.iamjunhyeok.petSitterAndWalker.exception.InvalidVerificationCodeException;
import com.iamjunhyeok.petSitterAndWalker.exception.LimitExceededException;
import com.iamjunhyeok.petSitterAndWalker.repository.VerificationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional
@Service
public class VerificationService {

    private final DefaultMessageService messageService;

    private final VerificationRepository verificationRepository;

    private final EntityManager entityManager;

    @Value("${coolsms.from}")
    private String from;

    public SingleMessageSentResponse sendVerificationCode(HttpServletRequest httpServletRequest, VerificationRequest request) {
        String phoneNumber = request.getPhoneNumber();
        String ipAddress = getClientIpAddress(httpServletRequest);
        LocalDate today = LocalDate.now();
        long count = verificationRepository.countByPhoneNumberOrIpAddressToday(phoneNumber, ipAddress, today);
        if (count >= 3) {
            throw new LimitExceededException("Only request up to 3 times per day.");
        }

        String verificationCode = generateVerificationCode();

        Verification verification = new Verification(phoneNumber, verificationCode, ipAddress);
        verificationRepository.save(verification);

        Message message = new Message();
        message.setFrom(from);
        message.setTo(phoneNumber);
        message.setText(String.format("인증 번호는 [%s]입니다.", verificationCode));

        SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
        return response;
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("HTTP_FORWARDED");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("HTTP_VIA");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("REMOTE_ADDR");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public void verify(VerifyRequest request) {
        LocalDateTime from = LocalDateTime.now().minusMinutes(3);
        Verification verification = verificationRepository.findFirstByPhoneNumberAndCreatedDateGreaterThanOrderByCreatedDateDesc(request.getPhoneNumber(), from)
                .orElseThrow(() -> new EntityNotFoundException("Verification code does not exist."));
        if (!request.getVerificationCode().equals(verification.getVerificationCode())) {
            throw new InvalidVerificationCodeException("Invalid verification code.");
        }
        verification.changeStatus(VerificationStatus.COMPLETE);
    }
}
