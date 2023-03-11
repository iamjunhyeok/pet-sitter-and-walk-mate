package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.UserInfoUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.UserInfoUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.UserJoinRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.UserJoinResponse;
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

    @Transactional
    public UserInfoUpdateResponse userInfoUpdate(UserInfoUpdateRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Cannot find user with userId : %d", userId)));
        user.updateUserInfo(request.getName(), request.getPhoneNumber(), request.getZipCode(), request.getAddress1(), request.getAddress2());
        return UserInfoUpdateResponse.builder()
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .zipCode(user.getZipCode())
                .address1(user.getAddress1())
                .address2(user.getAddress2())
                .build();
    }
}
