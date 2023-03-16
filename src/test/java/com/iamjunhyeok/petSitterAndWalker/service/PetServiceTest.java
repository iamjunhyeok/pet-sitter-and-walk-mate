package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.Gender;
import com.iamjunhyeok.petSitterAndWalker.constants.enums.PetPropertyEnum;
import com.iamjunhyeok.petSitterAndWalker.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.domain.Pet;
import com.iamjunhyeok.petSitterAndWalker.domain.PetProperty;
import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.PetRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.PetRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.PetViewResponse;
import com.iamjunhyeok.petSitterAndWalker.repository.PetPropertyRepository;
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
import java.util.Arrays;
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

    @Mock
    private PetPropertyRepository petPropertyRepository;

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
                .description("반달가슴곰")
                .images(Collections.singletonList(new MockMultipartFile("files", "image1.jpg", MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{})))
                .build();

        User user = User.builder()
                .id(1L)
                .build();
        User mockUser = mock(User.class);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(mockUser));

        PetProperty petType = new PetProperty(PetPropertyEnum.TYPE, "강아지");
        when(petPropertyRepository.findById(any())).thenReturn(Optional.of(petType));

        Pet pet = Pet.builder()
                .id(2L)
                .name(request.getName())
                .breed(request.getBreed())
                .age(request.getAge())
                .gender(Gender.valueOf(request.getGender()))
                .isNeutered(request.isNeutered())
                .weight(request.getWeight())
                .description(request.getDescription())
                .petType(petType)
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
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(request.getImages().size(), response.getImages().size());
        verify(mockUser, times(1)).registerPet(pet);
    }

    @Test
    @DisplayName("사용자의 애완동물 조회")
    void testWhenGetUserPet() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .build();

        Image image1 = new Image("image1.jpg");
        Image image2 = new Image("image2.jpg");

        Pet pet = Pet.builder()
                .id(2L)
                .name("후추")
                .breed("포메라니안")
                .age(4)
                .gender(Gender.FEMALE)
                .isNeutered(false)
                .weight(2)
                .description("반달가슴곰")
                .user(user)
                .build();
        pet.addImage(image2);
        pet.addImage(image1);

        List<Pet> pets = Arrays.asList(pet);
        when(petRepository.findByUserId(user.getId())).thenReturn(pets);

        // Act
        List<PetViewResponse> resultList = petService.viewMyPets(user);

        // Assert
        assertNotNull(resultList);
        assertEquals(pets.size(), resultList.size());
        assertEquals(pets.get(0).getId(), resultList.get(0).getId());
        assertEquals(pets.get(0).getName(), resultList.get(0).getName());
        assertEquals(image2.getName(), resultList.get(0).getImageName());
        verify(petRepository, times(1)).findByUserId(user.getId());
    }
}
