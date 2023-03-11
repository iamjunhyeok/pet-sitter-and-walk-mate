package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.UserInfoUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.UserInfoUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.UserJoinRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.UserJoinResponse;
import com.iamjunhyeok.petSitterAndWalker.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private UserJoinRequest request;

    private User user;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        request = UserJoinRequest.builder()
                .name("전준혁")
                .email("jeonjhyeok@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("13169")
                .address1("경기 성남시")
                .address2("1층")
                .build();

        user = User.builder()
                .id(1L)
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .zipCode(request.getZipCode())
                .address1(request.getAddress1())
                .address2(request.getAddress2())
                .build();
    }

    @Test
    @DisplayName("회원 가입")
    void testWhenValidJoin() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserJoinResponse response = userService.join(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(request.getZipCode(), response.getZipCode());
        assertEquals(request.getAddress1(), response.getAddress1());
        assertEquals(request.getAddress2(), response.getAddress2());
    }

    @Test
    @DisplayName("중복된 이메일")
    void testWhenDuplicateEmail() {
        // Arrange
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(EntityExistsException.class, () -> userService.join(request));
    }

    @Test
    @DisplayName("사용자 정보 업데이트")
    void testWhenValidUserInfo() {
        // Arrange
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .build();
        UserInfoUpdateRequest request = UserInfoUpdateRequest.builder()
                .name("변경한이름")
                .phoneNumber("01098765432")
                .zipCode("13180")
                .address1("경기 수원시")
                .address2("1234")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserInfoUpdateResponse response = userService.userInfoUpdate(request, userId);

        // Assert
        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(request.getZipCode(), response.getZipCode());
        assertEquals(request.getAddress1(), response.getAddress1());
        assertEquals(request.getAddress2(), response.getAddress2());
    }
}
