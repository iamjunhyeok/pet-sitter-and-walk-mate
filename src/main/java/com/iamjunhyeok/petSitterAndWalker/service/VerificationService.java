package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.dto.VerifyRequest;
import com.iamjunhyeok.petSitterAndWalker.exception.InvalidVerificationCodeException;
import com.iamjunhyeok.petSitterAndWalker.exception.TooManyRequestsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class VerificationService {

    private final DefaultMessageService messageService;

    private final UtilService utilService;

    private final RedissonClient redissonClient;

    @Value("${coolsms.from}")
    private String from;

    private static final String PREFIX_SMS = "sms:";
    private static final String SUFFIX_CODE = ":code";
    private static final String SUFFIX_COUNT = ":count";
    private static final int TIME_TO_LIVE = 3;
    private static final int REQUEST_LIMIT = 3;

    public SingleMessageSentResponse send(HttpServletRequest httpServletRequest, String phoneNumber) {
        String ipAddress = utilService.getClientIpAddress(httpServletRequest);
        log.info("핸드폰 번호와 IP 주소로 인증번호 생성 : {}, {}", phoneNumber, ipAddress);

        String phoneNumberCountKey = PREFIX_SMS + phoneNumber + SUFFIX_COUNT;
        String ipAddressCountKey = PREFIX_SMS + ipAddress + SUFFIX_COUNT;

        incrementCountAndCheckLimit(phoneNumberCountKey, String.format("핸드폰 번호에 대한 인증번호 요청 횟수를 초과함 : %s", phoneNumberCountKey));
        incrementCountAndCheckLimit(ipAddressCountKey, String.format("IP 주소에 대한 인증번호 요청 횟수를 초과함 : %s", ipAddressCountKey));

        String verificationCode = generateVerificationCode();

        String key = PREFIX_SMS + phoneNumber + SUFFIX_CODE;
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(verificationCode, TIME_TO_LIVE, TimeUnit.MINUTES);

        return getSingleMessageSentResponse(phoneNumber, verificationCode);
    }

    @NotNull
    public static String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1000000));
    }

    @NotNull
    private SingleMessageSentResponse getSingleMessageSentResponse(String phoneNumber, String verificationCode) {
        log.info("인증번호 전송 요청 : {}", phoneNumber);
        Message message = new Message();
        message.setFrom(from);
        message.setTo(phoneNumber);
        message.setText(String.format("인증 번호는 [%s]입니다.", verificationCode));
        SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
        log.info("인증번호 발송 완료 : {}", phoneNumber);
        return response;
    }

    private void incrementCountAndCheckLimit(String key, String exceptionMessage) {
        RAtomicLong count = redissonClient.getAtomicLong(key);
        if (count.get() >= REQUEST_LIMIT) {
            throw new TooManyRequestsException(exceptionMessage);
        }
        if (count.isExists()) {
            count.incrementAndGet();
        } else {
            count.set(1);
            Instant expire = Instant.now()
                    .plus(1, ChronoUnit.DAYS);
            count.expire(expire);
        }
    }

    public void verify(VerifyRequest request) {
        String phoneNumber = request.getPhoneNumber();
        log.info("인증번호 검증 요청 : {}", phoneNumber);

        String key = PREFIX_SMS + phoneNumber + SUFFIX_CODE;
        RBucket<String> bucket = redissonClient.getBucket(key);

        if (!bucket.isExists()) {
            throw new IllegalStateException(String.format("인증번호가 요청되지 않음 : %s", phoneNumber));
        } else if (!bucket.get().equals(request.getVerificationCode())) {
            throw new InvalidVerificationCodeException(String.format("인증번호가 일치하지 않음 : %s", phoneNumber));
        }
        bucket.delete();
        log.info("인증 성공 : {}", phoneNumber);
    }
}
