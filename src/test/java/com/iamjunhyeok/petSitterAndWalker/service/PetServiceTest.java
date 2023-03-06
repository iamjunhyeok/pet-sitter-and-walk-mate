package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.constants.Gender;
import com.iamjunhyeok.petSitterAndWalker.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.domain.Pet;
import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.PetRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.PetRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.repository.PetRepository;
import com.iamjunhyeok.petSitterAndWalker.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {

    @InjectMocks
    private PetService petService;

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Service s3Service;

    @Test
    @DisplayName("애완동물 등록")
    void testWhenValidPetRegister() {
        // Arrange
        PetRegisterRequest request = PetRegisterRequest.builder()
                .name("후추")
                .breed("포메라니안")
                .age(4)
                .gender(Gender.FEMALE.name())
                .isNeutered(false)
                .weight(2)
                .intro("반달가슴곰")
                .images(Collections.singletonList(new MockMultipartFile("files", "image1.jpg", MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{})))
                .build();

        User user = User.builder()
                .id(1L)
                .build();
        User mockUser = mock(User.class);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(mockUser));

        Pet pet = Pet.builder()
                .id(2L)
                .name(request.getName())
                .breed(request.getBreed())
                .age(request.getAge())
                .gender(Gender.valueOf(request.getGender()))
                .isNeutered(request.isNeutered())
                .weight(request.getWeight())
                .intro(request.getIntro())
                .build();
        when(petRepository.save(any(Pet.class))).thenReturn(pet);

        List<Image> images = new ArrayList<>();
        images.add(new Image("image1.jpg"));
        when(s3Service.uploadImage(anyList())).thenReturn(images);

        // Act
        PetRegisterResponse response = petService.register(user.getId(), request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getBreed(), response.getBreed());
        assertEquals(request.getAge(), response.getAge());
        assertEquals(request.getGender(), response.getGender());
        assertEquals(request.isNeutered(), response.isNeutered());
        assertEquals(request.getWeight(), response.getWeight());
        assertEquals(request.getIntro(), response.getIntro());
        assertEquals(request.getImages().size(), response.getImages().size());
        verify(mockUser, times(1)).registerPet(pet);
    }
}
