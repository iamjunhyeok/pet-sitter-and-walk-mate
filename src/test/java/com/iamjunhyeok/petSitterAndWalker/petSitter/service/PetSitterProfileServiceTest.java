package com.iamjunhyeok.petSitterAndWalker.petSitter.service;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.PetPropertyEnum;
import com.iamjunhyeok.petSitterAndWalker.image.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.image.service.S3Service;
import com.iamjunhyeok.petSitterAndWalker.pet.domain.PetProperty;
import com.iamjunhyeok.petSitterAndWalker.pet.repository.PetPropertyRepository;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitter;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterOption;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.MyPetSitterInfoRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.MyPetSitterInfoUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.MyPetSitterInfoUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.MyPetSitterInfoViewResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterOptionRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.repository.PetSitterRepository;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import com.iamjunhyeok.petSitterAndWalker.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetSitterProfileServiceTest {

    @InjectMocks
    private PetSitterProfileService petSitterProfileService;

    @Mock
    private PetSitterRepository petSitterRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PetPropertyRepository petPropertyRepository;

    @Spy
    private PetSitterMapper petSitterMapper;

    private List<PetProperty> petTypes;
    private List<PetProperty> petSizes;
    private List<PetSitterOption> options;
    private List<Image> images;
    private List<Image> deleteImages;
    private List<Image> newImages;
    private PetSitter petSitter;
    private User user;

    @BeforeEach
    void setup() {
        petTypes = Arrays.asList(
                new PetProperty(PetPropertyEnum.TYPE, "강아지"),
                new PetProperty(PetPropertyEnum.TYPE, "고양이")
        );
        petSizes = Arrays.asList(
                new PetProperty(PetPropertyEnum.SIZE, "소형"),
                new PetProperty(PetPropertyEnum.SIZE, "중형")
        );
        options = Arrays.asList(
                new PetSitterOption("목욕", "목욕시켜줌", 5000),
                new PetSitterOption("발톱정리", "발톱깎아줌", 3000)
        );
        Image image = new Image("image1.png");
        images = Arrays.asList(
                image,
                new Image("image2.png")
        );
        deleteImages = Arrays.asList(image);
        newImages = Arrays.asList(
                new Image("image3.png")
        );
        petSitter = new PetSitter("hello", 5);
        petSitter.addPetType(petTypes);
        petSitter.addPetSize(petSizes);
        petSitter.addOption(options);
        petSitter.addImage(images);

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
        user.registerPetSitterInfo(petSitter);
    }

    @Test
    @DisplayName("내 펫 시터 정보 조회")
    void testViewMyPetSitterInfo() {
        // Arrange
        when(petSitterRepository.findByUserId(user.getId())).thenReturn(Optional.ofNullable(petSitter));

        // Act
        MyPetSitterInfoViewResponse response = petSitterProfileService.viewMyPetSitterInfo(user);

        // Assert
        assertEquals(petSitter.getIntroduction(), response.getIntroduction());
        assertEquals(petSitter.getPetTypes().size(), response.getPetTypes().size());
        assertEquals(petSitter.getPetSizes().size(), response.getPetSizes().size());
        assertEquals(petSitter.getOptions().size(), response.getOptions().size());
        assertEquals(petSitter.getImages().size(), response.getImages().size());
    }

    @Test
    @DisplayName("내 펫 시터 정보가 존재하지 않음")
    void testWhenNoPetSitterInfo() {
        // Arrange
        when(petSitterRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> petSitterProfileService.viewMyPetSitterInfo(user));
    }

    @Test
    @DisplayName("펫 시터 정보 등록")
    void testRegisterMyPetSitterInfo() {
        // Arrange
        List<Long> petTypeIds = Arrays.asList(1L, 2L);
        List<Long> petSizeIds = Arrays.asList(3L, 4L);
        List<PetSitterOptionRequest> options = Arrays.asList(
                new PetSitterOptionRequest("목욕", "목욕시켜줌", 5000),
                new PetSitterOptionRequest("발톱정리", "발톱깎아줌", 3000)
        );
        List<MultipartFile> files = Arrays.asList(
                new MockMultipartFile("files", "image1.png", MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{}),
                new MockMultipartFile("files", "image2.png", MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{})
        );
        MyPetSitterInfoRegisterRequest request = MyPetSitterInfoRegisterRequest.builder()
                .introduction(petSitter.getIntroduction())
                .petTypeIds(petTypeIds)
                .petSizeIds(petSizeIds)
                .options(options)
                .images(files)
                .build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(petPropertyRepository.findAllById(request.getPetTypeIds())).thenReturn(petTypes);
        when(petPropertyRepository.findAllById(request.getPetSizeIds())).thenReturn(petSizes);
        when(s3Service.uploadImage(request.getImages())).thenReturn(images);

        // Act
        PetSitterRegisterResponse response = petSitterProfileService.registerMyPetSitterInfo(request, user);

        // Assert
        assertEquals(petSitter.getIntroduction(), response.getIntroduction());
        assertEquals(petSitter.getPetTypes().size(), response.getPetTypes().size());
        assertEquals(petSitter.getPetSizes().size(), response.getPetSizes().size());
        assertEquals(petSitter.getOptions().size(), response.getOptions().size());
        assertEquals(petSitter.getImages().size(), response.getImages().size());
    }

    @Test
    @DisplayName("사용자가 존재하지 않음")
    void testWhenNoUser() {
        // Arrange
        List<Long> petTypeIds = Arrays.asList(1L, 2L);
        List<Long> petSizeIds = Arrays.asList(3L, 4L);
        List<PetSitterOptionRequest> options = Arrays.asList(
                new PetSitterOptionRequest("목욕", "목욕시켜줌", 5000),
                new PetSitterOptionRequest("발톱정리", "발톱깎아줌", 3000)
        );
        List<MultipartFile> files = Arrays.asList(
                new MockMultipartFile("files", "image1.png", MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{}),
                new MockMultipartFile("files", "image2.png", MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{})
        );
        MyPetSitterInfoRegisterRequest request = MyPetSitterInfoRegisterRequest.builder()
                .introduction(petSitter.getIntroduction())
                .petTypeIds(petTypeIds)
                .petSizeIds(petSizeIds)
                .options(options)
                .images(files)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> petSitterProfileService.registerMyPetSitterInfo(request, user));
    }

    @Test
    @DisplayName("내 펫 시터 정보 수정")
    void testUpdateMyPetSitterInfo() {
        // Arrange
        List<Long> petTypeIds = Arrays.asList(1L, 2L);
        List<Long> petSizeIds = Arrays.asList(3L, 4L);
        List<PetSitterOptionRequest> options = Arrays.asList(
                new PetSitterOptionRequest("목욕", "목욕시켜줌", 5000),
                new PetSitterOptionRequest("발톱정리", "발톱깎아줌", 3000)
        );
        List<MultipartFile> files = Arrays.asList(
                new MockMultipartFile("files", "image1.png", MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{}),
                new MockMultipartFile("files", "image2.png", MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{})
        );
        List<Long> deleteImageIds = Arrays.asList(1L);
        MyPetSitterInfoUpdateRequest request = MyPetSitterInfoUpdateRequest.builder()
                .introduction(petSitter.getIntroduction())
                .petTypeIds(petTypeIds)
                .petSizeIds(petSizeIds)
                .options(options)
                .images(files)
                .deleteImageIds(deleteImageIds)
                .build();

        when(petSitterRepository.findByUserId(user.getId())).thenReturn(Optional.ofNullable(petSitter));
        when(petPropertyRepository.findAllById(request.getPetTypeIds())).thenReturn(petTypes);
        when(petPropertyRepository.findAllById(request.getPetSizeIds())).thenReturn(petSizes);
        when(s3Service.deleteImageById(request.getDeleteImageIds())).thenReturn(deleteImages);
        when(s3Service.uploadImage(request.getImages())).thenReturn(newImages);

        // Act
        MyPetSitterInfoUpdateResponse response = petSitterProfileService.updateMyPetSitterInfo(request, user);

        // Assert
        assertEquals(request.getIntroduction(), response.getIntroduction());
        assertEquals(request.getPetTypeIds().size(), response.getPetTypes().size());
        assertEquals(request.getPetSizeIds().size(), response.getPetSizes().size());
        assertEquals(4, response.getOptions().size());
        assertEquals(2, response.getImages().size());
    }
}