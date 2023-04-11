package com.iamjunhyeok.petSitterAndWalker.pet.service;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.Gender;
import com.iamjunhyeok.petSitterAndWalker.constants.enums.PetPropertyEnum;
import com.iamjunhyeok.petSitterAndWalker.image.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.image.service.S3Service;
import com.iamjunhyeok.petSitterAndWalker.pet.domain.Pet;
import com.iamjunhyeok.petSitterAndWalker.pet.domain.PetProperty;
import com.iamjunhyeok.petSitterAndWalker.pet.dto.MyPetAddRequest;
import com.iamjunhyeok.petSitterAndWalker.pet.dto.MyPetAddResponse;
import com.iamjunhyeok.petSitterAndWalker.pet.dto.MyPetListResponse;
import com.iamjunhyeok.petSitterAndWalker.pet.dto.MyPetUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.pet.dto.MyPetUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.pet.dto.MyPetViewResponse;
import com.iamjunhyeok.petSitterAndWalker.pet.repository.PetPropertyRepository;
import com.iamjunhyeok.petSitterAndWalker.pet.repository.PetRepository;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @InjectMocks
    private PetService petService;

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetPropertyRepository petPropertyRepository;

    @Mock
    private S3Service s3Service;

    private User user;
    private PetProperty petType;
    private List<Pet> pets;
    private List<MultipartFile> files;
    private List<Image> images;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .name("사용자")
                .email("jeonjhyeok@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기 성남시")
                .address2("1층")
                .build();
        petType = new PetProperty(PetPropertyEnum.TYPE, "강아지");
        Pet pet1 = Pet.builder()
                .id(1L)
                .name("애기")
                .breed("말티즈")
                .age(13)
                .gender(Gender.MALE)
                .weight(3)
                .description("별")
                .petType(petType)
                .build();
        Pet pet2 = Pet.builder()
                .id(2L)
                .name("후추")
                .breed("포메라니안")
                .age(4)
                .gender(Gender.FEMALE)
                .weight(3)
                .description("반달가슴곰")
                .petType(petType)
                .build();
        pets = Arrays.asList(pet1, pet2);
        files = Arrays.asList(
                new MockMultipartFile("files", "image1.png", MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{}),
                new MockMultipartFile("files", "image2.png", MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{})
        );
        images = Arrays.asList(
                new Image("image1.png"),
                new Image("image2.png")
        );
    }

    @Test
    @DisplayName("내 애완동물 목록 조회")
    void testMyPetList() {
        // Arrange
        when(petRepository.findByUserId(user.getId())).thenReturn(pets);

        // Act
        List<MyPetListResponse> responses = petService.getMyPets(user);

        // Assert
        assertEquals(pets.size(), responses.size());
        assertEquals(pets.get(0).getName(), responses.get(0).getName());
        assertEquals(pets.get(1).getName(), responses.get(1).getName());
    }

    @Test
    @DisplayName("내 애완동물 추가")
    void testAddMyPet() {
        // Arrange
        MyPetAddRequest request = MyPetAddRequest.builder()
                .petTypeId(1L)
                .gender(Gender.FEMALE.name())
                .name("후추")
                .breed("포메라니안")
                .age(4)
                .weight(3)
                .description("반달가슴곰")
                .images(files)
                .build();

        when(petPropertyRepository.findById(request.getPetTypeId())).thenReturn(Optional.ofNullable(petType));
        when(petRepository.save(any(Pet.class))).thenReturn(pets.get(1));
        when(s3Service.uploadImage(request.getImages())).thenReturn(images);

        // Act
        MyPetAddResponse response = petService.addMyPet(request, user);

        // Assert
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getBreed(), response.getBreed());
        assertEquals(request.getAge(), response.getAge());
        assertEquals(request.getGender(), response.getGender().name());
        assertEquals(request.getWeight(), response.getWeight());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(petType.getName(), response.getPetType().getName());
        assertEquals(images.get(0).getName(), response.getImages().get(0).getName());
    }

    @Test
    @DisplayName("펫 타입이 존재하지 않을 때")
    void testWhenPetTypeDoesNotExist() {
        // Arrange
        MyPetAddRequest request = MyPetAddRequest.builder()
                .petTypeId(1L)
                .gender(Gender.FEMALE.name())
                .name("후추")
                .breed("포메라니안")
                .age(4)
                .weight(3)
                .description("반달가슴곰")
                .images(files)
                .build();
        when(petPropertyRepository.findById(request.getPetTypeId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> petService.addMyPet(request, user));
    }

    @Test
    @DisplayName("내 애완동물 삭제")
    void testDeleteMyPet() {
        // Arrange
        Long petId = 1L;
        when(petRepository.findByIdAndUserId(petId, user.getId())).thenReturn(Optional.ofNullable(pets.get(0)));

        // Act
        petService.deleteMyPet(petId, user);

        // Assert
        assertTrue(pets.get(0).isDeleted());
    }

    @Test
    @DisplayName("애완동물이 존재하지 않을 때")
    void testWhenPetsDoNotExist() {
        // Arrange
        Long petId = 1L;
        when(petRepository.findByIdAndUserId(petId, user.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> petService.deleteMyPet(petId, user));
    }

    @Test
    @DisplayName("내 애완동물 정보 수정")
    void testUpdateMyPet() {
        // Arrange
        MyPetUpdateRequest request = MyPetUpdateRequest.builder()
                .deleteImageIds(Arrays.asList(1L))
                .name("후추")
                .breed("포메라니안")
                .age(4)
                .weight(3)
                .description("반달가슴곰입니다")
                .images(Arrays.asList(new MockMultipartFile("files", "image3.png", MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{})))
                .build();
        List<Image> deleteImages = Arrays.asList(images.get(0));

        pets.get(1).addImage(images);

        Long petId = 2L;
        when(petRepository.findByIdAndUserId(petId, user.getId())).thenReturn(Optional.ofNullable(pets.get(1)));
        when(s3Service.deleteImageById(request.getDeleteImageIds())).thenReturn(deleteImages);
        when(s3Service.uploadImage(request.getImages())).thenReturn(Arrays.asList(new Image("image3.png")));

        // Act
        MyPetUpdateResponse response = petService.updateMyPet(request, pets.get(1).getId(), user);

        // Assert
        assertEquals(pets.get(1).getId(), response.getId());
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getBreed(), response.getBreed());
        assertEquals(request.getAge(), response.getAge());
        assertEquals(request.getWeight(), response.getWeight());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(2, response.getImages().size());
    }

    @Test
    @DisplayName("애완동물 조회")
    void testGetMyPet() {
        // Arrange
        Long petId = 2L;
        when(petRepository.findById(petId)).thenReturn(Optional.ofNullable(pets.get(1)));

        // Act
        MyPetViewResponse response = petService.getMyPet(petId);

        // Assert
        assertEquals(pets.get(1).getId(), response.getId());
        assertEquals(pets.get(1).getName(), response.getName());
        assertEquals(pets.get(1).getBreed(), response.getBreed());
        assertEquals(pets.get(1).getAge(), response.getAge());
        assertEquals(pets.get(1).getGender(), response.getGender());
        assertEquals(pets.get(1).getWeight(), response.getWeight());
        assertEquals(pets.get(1).getDescription(), response.getDescription());
        assertEquals(pets.get(1).getPetType().getName(), response.getPetType().getName());
        assertEquals(pets.get(1).getImages().size(), response.getImages().size());
    }
}