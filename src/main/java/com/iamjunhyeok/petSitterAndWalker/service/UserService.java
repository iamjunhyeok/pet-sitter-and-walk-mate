package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.UserJoinRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.UserJoinResponse;
import com.iamjunhyeok.petSitterAndWalker.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
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
            throw new EntityExistsException();
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
}
