package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.MyInfoViewResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.UserInfoUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.UserInfoUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.UserJoinRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.UserJoinResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.UserPasswordChangeRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.UserPasswordChangeResponse;
import com.iamjunhyeok.petSitterAndWalker.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UserJoinResponse join(UserJoinRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EntityExistsException(String.format("This email already exists : %s", request.getEmail()));
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

        return UserJoinResponse.builder()
                .id(save.getId())
                .name(save.getName())
                .email(save.getEmail())
                .phoneNumber(save.getPhoneNumber())
                .zipCode(save.getZipCode())
                .address1(save.getAddress1())
                .address2(save.getAddress2())
                .build();
    }

    public MyInfoViewResponse viewMyInfo(User user) {
        User findUser = userRepository.findById(user.getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("User ID does not exist : %s", user.getId())));
        return MyInfoViewResponse.builder()
                .name(findUser.getName())
                .phoneNumber(findUser.getPhoneNumber())
                .zipCode(findUser.getZipCode())
                .address1(findUser.getAddress1())
                .address2(findUser.getAddress2())
                .build();
    }

    @Transactional
    public UserInfoUpdateResponse updateMyInfo(UserInfoUpdateRequest request, User user) {
        userRepository.updateMyInfo(request.getName(), request.getPhoneNumber(), request.getZipCode(), request.getAddress1(), request.getAddress2(), user.getId());
        return UserInfoUpdateResponse.builder()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .zipCode(request.getZipCode())
                .address1(request.getAddress1())
                .address2(request.getAddress2())
                .build();
    }

    @Transactional
    public UserPasswordChangeResponse changePassword(UserPasswordChangeRequest request, User user) {
        String userPassword = userRepository.getPasswordById(user.getId());
        if (!passwordEncoder.matches(request.getOldPassword(), userPassword)) {
            throw new IllegalArgumentException("Wrong password");
        }
        String newPassword = passwordEncoder.encode(request.getNewPassword());
        userRepository.updatePasswordById(passwordEncoder.encode(newPassword), user.getId());
        return new UserPasswordChangeResponse(newPassword);
    }

    @Transactional
    public void follow(Long userId, User user) {
        User userEntity = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("존재하지 않는 사용자 : %s", user.getId())));
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("존재하지 않는 사용자를 팔로우 함 : %s", userId)));
        userEntity.follow(target);
    }
}
