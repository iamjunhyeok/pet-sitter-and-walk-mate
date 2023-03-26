package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.VerificationStatus;
import com.iamjunhyeok.petSitterAndWalker.domain.Verification;
import com.iamjunhyeok.petSitterAndWalker.dto.VerificationRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.VerifyRequest;
import com.iamjunhyeok.petSitterAndWalker.exception.InvalidVerificationCodeException;
import com.iamjunhyeok.petSitterAndWalker.exception.LimitExceededException;
import com.iamjunhyeok.petSitterAndWalker.exception.SendVerificationCodeException;
import com.iamjunhyeok.petSitterAndWalker.repository.VerificationRepository;
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

    private final UtilService utilService;

    @Value("${coolsms.from}")
    private String from;

    public SingleMessageSentResponse sendVerificationCode(HttpServletRequest httpServletRequest, VerificationRequest request) {
        String phoneNumber = request.getPhoneNumber();
        String ipAddress = utilService.getClientIpAddress(httpServletRequest);
        LocalDate today = LocalDate.now();
        long count = verificationRepository.countByPhoneNumberOrIpAddressToday(phoneNumber, ipAddress, today);
        if (count >= 3) {
            throw new LimitExceededException("Only request up to 3 times per day.");
        }

        String verificationCode = generateVerificationCode();

        Message message = new Message();
        message.setFrom(from);
        message.setTo(phoneNumber);
        message.setText(String.format("인증 번호는 [%s]입니다.", verificationCode));

        try {
            SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));

            verificationRepository.updateStatusByPhoneNumber(phoneNumber, VerificationStatus.INVALID);

            Verification verification = new Verification(phoneNumber, verificationCode, ipAddress);
            verification.changeStatus(VerificationStatus.WAITING);
            verificationRepository.save(verification);

            return response;
        } catch (Exception e) {
            throw new SendVerificationCodeException();
        }
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    public void verify(VerifyRequest request) {
        LocalDateTime from = LocalDateTime.now().minusMinutes(3);
        Verification verification = verificationRepository.findFirstByPhoneNumberAndStatusAndCreatedDateGreaterThanOrderByCreatedDateDesc(request.getPhoneNumber(), VerificationStatus.WAITING, from)
                .orElseThrow(() -> new EntityNotFoundException("Verification code does not exist."));
        if (!request.getVerificationCode().equals(verification.getVerificationCode())) {
            throw new InvalidVerificationCodeException("Invalid verification code.");
        }
        verification.changeStatus(VerificationStatus.COMPLETED);
    }
}
