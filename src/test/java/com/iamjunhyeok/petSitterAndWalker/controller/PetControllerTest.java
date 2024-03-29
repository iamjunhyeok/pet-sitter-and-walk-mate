package com.iamjunhyeok.petSitterAndWalker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iamjunhyeok.petSitterAndWalker.constants.enums.Gender;
import com.iamjunhyeok.petSitterAndWalker.pet.dto.MyPetAddRequest;
import com.iamjunhyeok.petSitterAndWalker.user.dto.UserJoinRequest;
import com.iamjunhyeok.petSitterAndWalker.user.dto.UserJoinResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
                .build();
    }

    @Test
    @DisplayName("펫 등록")
    void testWhenValidPetRegister() throws Exception {
        // Arrange
        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .name("전준혁")
                .email("jeonjhyeok@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("13169")
                .address1("경기 성남시")
                .address2("1층")
                .build();
        RequestBuilder userJoinRequestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userJoinRequest));
        MvcResult mvcResult = mockMvc.perform(userJoinRequestBuilder).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UserJoinResponse userJoinResponse = new ObjectMapper().readValue(contentAsString, UserJoinResponse.class);

        MyPetAddRequest request = MyPetAddRequest.builder()
                .name("후추")
                .breed("포메라니안")
                .age(4)
                .gender(Gender.FEMALE.name())
                .isNeutered(false)
                .weight(2)
                .description("반달가슴곰")
                .petTypeId(1L)
                .build();

        MockMultipartFile file = new MockMultipartFile("files", "image1.jpg", MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{});

        byte[] bytes = new ObjectMapper().writeValueAsString(request).getBytes(Charset.defaultCharset());

        // Act & Assert
        RequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/users/" + userJoinResponse.getId() + "/pets")
                .file(file)
                .file(new MockMultipartFile("request", "", MediaType.APPLICATION_JSON_VALUE, bytes));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.breed").value(request.getBreed()))
                .andExpect(jsonPath("$.age").value(request.getAge()))
                .andExpect(jsonPath("$.gender").value(request.getGender()))
                .andExpect(jsonPath("$.neutered").value(request.isNeutered()))
                .andExpect(jsonPath("$.weight").value(request.getWeight()))
                .andExpect(jsonPath("$.images", hasSize(1)))
                .andDo(print());

    }
}
