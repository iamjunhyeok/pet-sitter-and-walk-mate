package com.iamjunhyeok.petSitterAndWalker.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iamjunhyeok.petSitterAndWalker.exception.ResourceAlreadyExistsException;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import com.iamjunhyeok.petSitterAndWalker.user.dto.MyInfoViewResponse;
import com.iamjunhyeok.petSitterAndWalker.user.dto.UserInfoUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.user.dto.UserInfoUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.user.dto.UserJoinRequest;
import com.iamjunhyeok.petSitterAndWalker.user.dto.UserJoinResponse;
import com.iamjunhyeok.petSitterAndWalker.user.dto.UserPasswordChangeRequest;
import com.iamjunhyeok.petSitterAndWalker.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @WithMockUser
    @Test
    @DisplayName("회원가입")
    void testJoin() throws Exception {
        // Arrange
        UserJoinRequest request = UserJoinRequest.builder()
                .name("사용자")
                .email("jeonjhyeok@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기 성남시")
                .address2("1층")
                .build();
        UserJoinResponse response = new UserJoinResponse();
        BeanUtils.copyProperties(request, response);
        when(userService.join(any(UserJoinRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        post("/join")
                                .with(csrf())
                                .content(new ObjectMapper().writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.phoneNumber").value(request.getPhoneNumber()))
                .andExpect(jsonPath("$.zipCode").value(request.getZipCode()))
                .andExpect(jsonPath("$.address1").value(request.getAddress1()))
                .andExpect(jsonPath("$.address2").value(request.getAddress2()))
                .andDo(print());
    }

    @WithMockUser
    @Test
    @DisplayName("중복된 이메일로 회원가입")
    void testWhenJoiningWithDuplicateEmails() throws Exception {
        // Arrange
        UserJoinRequest request = UserJoinRequest.builder()
                .name("사용자")
                .email("jeonjhyeok@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기 성남시")
                .address2("1층")
                .build();
        when(userService.join(any(UserJoinRequest.class))).thenThrow(new ResourceAlreadyExistsException(""));

        // Act & Assert
        mockMvc.perform(
                post("/join")
                        .with(csrf())
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isConflict());
    }

    @WithMockUser
    @Test
    @DisplayName("잘못된 입력 값")
    void testWhenInvalidInput() throws Exception {
        // Arrange
        UserJoinRequest request = UserJoinRequest.builder()
                .name("사용자")
                .email("jeonjhyeok")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기 성남시")
                .address2("1층")
                .build();

        // Act & Assert
        mockMvc.perform(
                post("/join")
                        .with(csrf())
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    @DisplayName("내 정보 조회")
    void testViewMyInfo() throws Exception {
        // Arrange
        User user = User.builder()
                .name("사용자")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기 성남시")
                .address2("1층")
                .build();
        MyInfoViewResponse response = new MyInfoViewResponse();
        BeanUtils.copyProperties(user, response);
        when(userService.viewMyInfo(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        get("/my-info")
                                .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.phoneNumber").value(user.getPhoneNumber()))
                .andExpect(jsonPath("$.zipCode").value(user.getZipCode()))
                .andExpect(jsonPath("$.address1").value(user.getAddress1()))
                .andExpect(jsonPath("$.address2").value(user.getAddress2()));
    }

    @WithMockUser
    @Test
    @DisplayName("내 정보 수정")
    void testUpdateMyInfo() throws Exception {
        // Arrange
        UserInfoUpdateRequest request = UserInfoUpdateRequest.builder()
                .name("사용자")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기 성남시")
                .address2("1층")
                .build();
        UserInfoUpdateResponse response = new UserInfoUpdateResponse();
        BeanUtils.copyProperties(request, response);
        when(userService.updateMyInfo(any(UserInfoUpdateRequest.class), any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        put("/my-info")
                                .with(csrf())
                                .content(new ObjectMapper().writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.phoneNumber").value(request.getPhoneNumber()))
                .andExpect(jsonPath("$.zipCode").value(request.getZipCode()))
                .andExpect(jsonPath("$.address1").value(request.getAddress1()))
                .andExpect(jsonPath("$.address2").value(request.getAddress2()));
    }

    @WithMockUser
    @Test
    @DisplayName("비밀번호 변경")
    void testChangePassword() throws Exception {
        // Arrange
        UserPasswordChangeRequest request = new UserPasswordChangeRequest("123", "456", "456");

        // Act & Assert
        mockMvc.perform(
                put("/password")
                        .with(csrf())
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    @DisplayName("팔로우")
    void testFollow() throws Exception {
        // Arrange
        Long userId = 1L;

        // Act & Assert
        mockMvc.perform(
                post("/users/{userId}/follow", userId)
                        .with(csrf())
        ).andExpect(status().isCreated());
    }

    @WithMockUser
    @Test
    @DisplayName("언팔로우")
    void testUnfollow() throws Exception {
        // Arrange
        Long userId = 1L;

        // Act & Assert
        mockMvc.perform(
                delete("/users/{userId}/unfollow", userId)
                        .with(csrf())
        ).andExpect(status().isNoContent());
    }
}