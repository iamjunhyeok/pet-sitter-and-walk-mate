package com.iamjunhyeok.petSitterAndWalker.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iamjunhyeok.petSitterAndWalker.constants.Security;
import com.iamjunhyeok.petSitterAndWalker.constants.enums.LoginStatus;
import com.iamjunhyeok.petSitterAndWalker.user.domain.LoginLog;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import com.iamjunhyeok.petSitterAndWalker.user.repository.LoginLogRepository;
import com.iamjunhyeok.petSitterAndWalker.common.service.UtilService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final CustomAuthenticationManager customAuthenticationManager;

    private final LoginLogRepository loginLogRepository;

    private final UtilService utilService;

    private static final String EMAIL = "email";

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);

            String clientIpAddress = utilService.getClientIpAddress(request);
            String userAgent = utilService.getUserAgent(request);
            loginLogRepository.save(new LoginLog(user.getEmail(), clientIpAddress, userAgent));

            request.setAttribute(EMAIL, user.getEmail());

            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
            return customAuthenticationManager.authenticate(authentication);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        User user = (User) authResult.getPrincipal();
        String token = JWT.create()
                .withSubject(String.valueOf(user.getId()))
                .withClaim("name", user.getName())
                .withClaim(EMAIL, user.getEmail())
                .withClaim("phoneNumber", user.getPhoneNumber())
                .withExpiresAt(new Date(System.currentTimeMillis() + Security.TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC512(secretKey));
        response.addHeader(Security.AUTHORIZATION, Security.BEARER + token);
        updateLoginStatus(user.getEmail(), LoginStatus.SUCCEED);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        updateLoginStatus((String) request.getAttribute(EMAIL), LoginStatus.FAILED);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private void updateLoginStatus(String email, LoginStatus status) {
        LoginLog loginLog = loginLogRepository.findByEmailAndStatus(email, LoginStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException(String.format("로그인 시도 정보를 찾을 수 없음 : %s", email)));
        loginLog.changeStatus(status);
        loginLogRepository.save(loginLog);
    }
}
