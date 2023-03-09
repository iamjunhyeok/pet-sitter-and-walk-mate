package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.domain.User;
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
import static org.mockito.ArgumentMatchers.any;
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
}
