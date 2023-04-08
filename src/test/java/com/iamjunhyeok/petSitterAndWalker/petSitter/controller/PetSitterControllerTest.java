package com.iamjunhyeok.petSitterAndWalker.petSitter.controller;

import com.iamjunhyeok.petSitterAndWalker.image.dto.ImageSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.pet.dto.PetPropertySimpleDto;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterInfoResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterListResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterOptionSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.petSitter.service.PetSitterService;
import com.iamjunhyeok.petSitterAndWalker.user.dto.UserSimpleDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PetSitterController.class)
class PetSitterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetSitterService petSitterService;

    @WithMockUser
    @Test
    @DisplayName("펫 시터 목록 조회")
    void testPetSitterList() throws Exception {
        // Arrange
        PetSitterListResponse petSitterResponse1 = new PetSitterListResponse("petSitter1", "경기 성남시", 5);
        PetSitterListResponse petSitterResponse2 = new PetSitterListResponse("petSitter2", "서울특별시", 3);
        List<PetSitterListResponse> list = Arrays.asList(petSitterResponse1, petSitterResponse2);
        Page<PetSitterListResponse> page = new PageImpl<>(list);
        when(petSitterService.getPetSitters(PageRequest.of(0, 2))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(
                        get("/pet-sitters")
                                .param("page", "0")
                                .param("size", "2")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value(petSitterResponse1.getName()))
                .andExpect(jsonPath("$.content[1].name").value(petSitterResponse2.getName()));
    }

    @WithMockUser
    @Test
    @DisplayName("펫 시터 정보 조회")
    void testPetSitterInfo() throws Exception {
        // Arrange
        Long petSitterId = 1L;
        PetSitterInfoResponse petSitterInfoResponse = PetSitterInfoResponse.builder()
                .id(petSitterId)
                .user(new UserSimpleDto(1L, "jeon"))
                .profileImage(new ImageSimpleDto(1L, "image1.png"))
                .introduction("hello")
                .reviews(0)
                .averageRating(0)
                .petTypes(Arrays.asList(new PetPropertySimpleDto(1L, "강아지", 0)))
                .petSizes(Arrays.asList(new PetPropertySimpleDto(1L, "소형", 0)))
                .options(Arrays.asList(new PetSitterOptionSimpleDto(1L, "목욕", 5000)))
                .images(Arrays.asList(new ImageSimpleDto(1L, "image2.png")))
                .build();
        when(petSitterService.getPetSitter(petSitterId)).thenReturn(petSitterInfoResponse);

        // Act & Assert
        mockMvc.perform(
                        get("/pet-sitters/{petSitterId}", petSitterId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(petSitterId))
                .andExpect(jsonPath("$.introduction").value(petSitterInfoResponse.getIntroduction()))
                .andExpect(jsonPath("$.user.name").value(petSitterInfoResponse.getUser().getName()))
                .andExpect(jsonPath("$.profileImage.name").value(petSitterInfoResponse.getProfileImage().getName()))
                .andExpect(jsonPath("$.petTypes.size()").value(petSitterInfoResponse.getPetTypes().size()))
                .andExpect(jsonPath("$.petSizes.size()").value(petSitterInfoResponse.getPetSizes().size()))
                .andExpect(jsonPath("$.options.size()").value(petSitterInfoResponse.getOptions().size()))
                .andExpect(jsonPath("$.images.size()").value(petSitterInfoResponse.getImages().size()));
    }
}