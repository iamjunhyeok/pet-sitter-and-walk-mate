package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.UserInfoUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.UserInfoUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.UserJoinRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.UserJoinResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.UserPasswordChangeRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.UserPasswordChangeResponse;
import com.iamjunhyeok.petSitterAndWalker.exception.PasswordMismatchException;
import com.iamjunhyeok.petSitterAndWalker.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    private Validator validator;

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

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
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

    @ParameterizedTest
    @DisplayName("회원 가입 - 이메일 형식이 올바르지 않을 때")
    @MethodSource
    void testWhenInvalidEmail(String email) {
        // Arrange
        request.setEmail(email);

        // Act
        Set<ConstraintViolation<UserJoinRequest>> validate = validator.validate(request);

        // Assert
        assertEquals(1, validate.size());
    }

    static Stream<Arguments> testWhenInvalidEmail() {
        return Stream.of(
                Arguments.of("jeonjhyeokgmail.com"),
                Arguments.of("jeonjhyeok@gmailcom"),
                Arguments.of("jeonjhyeok@gmail,com"),
                Arguments.of("jeonjhyeok@gmail/com"),
                Arguments.of("jeonjhyeokgmailcom")
        );
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

        // Act
        UserInfoUpdateResponse response = userService.updateMyInfo(request, user);

        // Assert
        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(request.getZipCode(), response.getZipCode());
        assertEquals(request.getAddress1(), response.getAddress1());
        assertEquals(request.getAddress2(), response.getAddress2());
        verify(userRepository, times(1)).updateMyInfo(request.getName(), request.getPhoneNumber(),
                request.getZipCode(), request.getAddress1(), request.getAddress2(), user.getId());
    }

    @Test
    @DisplayName("사용자 비밀번호 변경")
    void testWhenValidUserPasswordChange() {
        // Arrange
        Long userId = 1L;
        String oldPassword = "1231231";
        String newPassword = "1111";
        String retypeNewPassword = "1111";
        UserPasswordChangeRequest request = new UserPasswordChangeRequest(oldPassword, newPassword, retypeNewPassword);

        String encodedPassword = passwordEncoder.encode("1231231");
        when(userRepository.getPasswordById(userId)).thenReturn(encodedPassword);

        // Act
        UserPasswordChangeResponse response = userService.changePassword(request, user);

        // Assert
        assertNotNull(response);
        assertTrue(passwordEncoder.matches(request.getNewPassword(), response.getPassword()));
    }

    @Test
    @DisplayName("사용자 비밀번호 변경 - 새 비밀번호가 일치하지 않을 때")
    void testWhenNewPasswordMismatched() {
        // Arrange
        String newPassword = "1111";
        String retypeNewPassword = "2222";

        // Act & Assert
        assertThrows(PasswordMismatchException.class, () -> new UserPasswordChangeRequest(user.getPassword(), newPassword, retypeNewPassword));
    }

    @Test
    @DisplayName("사용자 비밀번호 변경 - 현재 비밀번호가 일치하지 않을 때")
    void testWhenOldPasswordIncorrect() {
        // Arrange
        Long userId = 1L;
        String oldPassword = "1q2w3e4r!";
        String newPassword = "1111";
        String retypeNewPassword = "1111";
        UserPasswordChangeRequest request = new UserPasswordChangeRequest(oldPassword, newPassword, retypeNewPassword);

        when(userRepository.getPasswordById(userId)).thenReturn("1231231");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.changePassword(request, user));
    }
}