package com.iamjunhyeok.petSitterAndWalker.user.service;

import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import com.iamjunhyeok.petSitterAndWalker.user.dto.MyInfoViewResponse;
import com.iamjunhyeok.petSitterAndWalker.user.dto.UserInfoUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.user.dto.UserInfoUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.user.dto.UserJoinRequest;
import com.iamjunhyeok.petSitterAndWalker.user.dto.UserJoinResponse;
import com.iamjunhyeok.petSitterAndWalker.user.dto.UserPasswordChangeRequest;
import com.iamjunhyeok.petSitterAndWalker.exception.PasswordMismatchException;
import com.iamjunhyeok.petSitterAndWalker.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private static final String NOT_EXIST_USER = "존재하지 않는 사용자 : %s";

    @Transactional
    public UserJoinResponse join(UserJoinRequest request) {
        log.info("회원가입 : {}", request.getEmail());
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EntityExistsException(String.format("이미 존재하는 이메일 : %s", request.getEmail()));
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .zipCode(request.getZipCode())
                .address1(request.getAddress1())
                .address2(request.getAddress2())
                .build();
        User save = userRepository.save(user);

        UserJoinResponse response = new UserJoinResponse();
        BeanUtils.copyProperties(save, response);

        log.info("회원가입 성공 : {}, UserId : {}", response.getEmail(), response.getId());
        return response;
    }

    public MyInfoViewResponse viewMyInfo(User user) {
        log.info("사용자 정보 조회 : {}", user.getId());
        User findUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_EXIST_USER, user.getId())));

        MyInfoViewResponse response = new MyInfoViewResponse();
        BeanUtils.copyProperties(findUser, response);

        log.info("사용자 정보 조회 성공 : {}", user.getId());
        return response;
    }

    @Transactional
    public UserInfoUpdateResponse updateMyInfo(UserInfoUpdateRequest request, User user) {
        log.info("사용자 정보 변경 : {}", user.getId());
        User findUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_EXIST_USER, user.getId())));

        findUser.updateUserInfo(request.getName(), request.getPhoneNumber(), request.getZipCode(), request.getAddress1(), request.getAddress2());

        UserInfoUpdateResponse response = new UserInfoUpdateResponse();
        BeanUtils.copyProperties(findUser, response);

        log.info("사용자 정보 변경 완료 : {}", user.getId());
        return response;
    }

    @Transactional
    public void changePassword(UserPasswordChangeRequest request, User user) {
        log.info("사용자 비밀번호 변경 : {}", user.getId());
        User findUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_EXIST_USER, user.getId())));

        if (!passwordEncoder.matches(request.getOldPassword(), findUser.getPassword())) {
            throw new PasswordMismatchException("기존 비밀번호가 데이터베이스에 저장된 비밀번호와 다름");
        }
        findUser.changePassword(passwordEncoder.encode(request.getNewPassword()));

        log.info("사용자 비밀번호 변경 완료 : {}", user.getId());
    }

    @Transactional
    public void followOrUnfollow(Long userId, User user, boolean isFollow) {
        log.info("사용자 {} : {}", isFollow ? "팔로우" : "언팔로우", userId);
        User userEntity = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_EXIST_USER, user.getId())));
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("존재하지 않는 사용자를 %s 함 : %s", isFollow ? "팔로우" : "언팔로우", userId)));

        if (isFollow) {
            userEntity.follow(target);
        } else {
            userEntity.unfollow(target);
        }

        log.info("사용자 {} 완료 : {}", isFollow ? "팔로우" : "언팔로우", target.getId());
    }
}
