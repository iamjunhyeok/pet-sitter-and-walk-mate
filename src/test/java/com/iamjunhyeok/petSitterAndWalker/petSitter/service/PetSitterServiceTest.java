package com.iamjunhyeok.petSitterAndWalker.petSitter.service;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.PetPropertyEnum;
import com.iamjunhyeok.petSitterAndWalker.image.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.image.dto.ImageSimpleDto;
import com.iamjunhyeok.petSitterAndWalker.image.service.S3Service;
import com.iamjunhyeok.petSitterAndWalker.pet.domain.PetProperty;
import com.iamjunhyeok.petSitterAndWalker.pet.repository.PetPropertyRepository;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitter;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterOption;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterInfoResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterListResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.repository.PetSitterRepository;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import com.iamjunhyeok.petSitterAndWalker.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetSitterServiceTest {

    @InjectMocks
    private PetSitterService petSitterService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PetPropertyRepository petPropertyRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private PetSitterRepository petSitterRepository;

    @Spy
    private PetSitterMapper petSitterMapper;

    @Test
    @DisplayName("펫 시터 목록 조회")
    void testGetPetSitters() {
        // Arrange
        List<ImageSimpleDto> images1 = Arrays.asList(
                new ImageSimpleDto(1L, "image1"),
                new ImageSimpleDto(2L, "image2")
        );
        List<ImageSimpleDto> images2 = Arrays.asList(
                new ImageSimpleDto(3L, "image3"),
                new ImageSimpleDto(4L, "image4")
        );
        PetSitterListResponse petSitterRes1 = new PetSitterListResponse("펫시터1", "경기", 5, images1);
        PetSitterListResponse petSitterRes2 = new PetSitterListResponse("펫시터2", "서울", 2, images2);
        List<PetSitterListResponse> petSitters = Arrays.asList(petSitterRes1, petSitterRes2);

        PetSitter petSitter1 = new PetSitter("hello1", 5);
        petSitter1.addImage(new Image("image1"));
        petSitter1.addImage(new Image("image2"));
        User user1 = User.builder()
                .name("펫시터1")
                .email("user1@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기")
                .address2("1층")
                .build();
        user1.registerPetSitterInfo(petSitter1);

        PetSitter petSitter2 = new PetSitter("hello2", 2);
        petSitter2.addImage(new Image("image3"));
        petSitter2.addImage(new Image("image4"));
        User user2 = User.builder()
                .name("펫시터2")
                .email("user1@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("서울")
                .address2("1층")
                .build();
        user2.registerPetSitterInfo(petSitter2);

        List<PetSitter> petSitterList = Arrays.asList(petSitter1, petSitter2);

        PageRequest pageRequest = PageRequest.of(0, 10);
        when(petSitterRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(petSitterList));

        // Act
        Page<PetSitterListResponse> findPetSitters = petSitterService.getPetSitters(pageRequest);

        // Assert
        assertEquals(petSitterList.size(), findPetSitters.getContent().size());
        assertEquals(petSitterList.get(0).getUser().getName(), findPetSitters.getContent().get(0).getName());
        assertEquals(petSitterList.get(0).getUser().getAddress1(), findPetSitters.getContent().get(0).getAddress());
        assertEquals(petSitterList.get(0).getAverageRating(), findPetSitters.getContent().get(0).getAverageRating());
        assertEquals(petSitterList.get(0).getImages().size(), findPetSitters.getContent().get(0).getImages().size());
    }

    @Test
    @DisplayName("사용자에게 펫 시터 정보가 존재하지 않음")
    void testWhenUserIsNotPetSitter() {
        // Arrange
        Long petSitterId = 1L;
        when(petSitterRepository.findById(petSitterId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> petSitterService.getPetSitter(petSitterId));
    }

    @Test
    @DisplayName("사용자의 펫 시터 정보 조회")
    void testGetPetSitter() {
        // Arrange
        Long petSitterId = 1L;
        PetSitter petSitter = new PetSitter("hello", 5);
        petSitter.addPetType(new PetProperty(PetPropertyEnum.TYPE, "강아지"));
        petSitter.addPetSize(new PetProperty(PetPropertyEnum.SIZE, "소형"));
        petSitter.addOption(new PetSitterOption("목욕", "목욕시켜줌", 5000));
        petSitter.addImage(new Image("image1.png"));
        User user = User.builder()
                .name("사용자")
                .email("jeonjhyeok@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기 성남시")
                .address2("1층")
                .build();
        user.registerPetSitterInfo(petSitter);

        when(petSitterRepository.findById(petSitterId)).thenReturn(Optional.of(petSitter));

        // Act
        PetSitterInfoResponse findPetSitter = petSitterService.getPetSitter(petSitterId);

        // Assert
        assertEquals(user.getName(), findPetSitter.getName());
        assertEquals(user.getAddress1(), findPetSitter.getAddress());
        assertEquals(petSitter.getIntroduction(), findPetSitter.getIntroduction());
        assertEquals(petSitter.getAverageRating(), findPetSitter.getAverageRating());
        assertEquals(petSitter.getPetTypes().size(), findPetSitter.getPetTypes().size());
        assertEquals(petSitter.getPetTypes().get(0).getPetProperty().getName(), findPetSitter.getPetTypes().get(0).getName());
        assertEquals(petSitter.getPetSizes().size(), findPetSitter.getPetSizes().size());
        assertEquals(petSitter.getPetSizes().get(0).getPetProperty().getName(), findPetSitter.getPetSizes().get(0).getName());
        assertEquals(petSitter.getOptions().size(), findPetSitter.getOptions().size());
        assertEquals(petSitter.getOptions().get(0).getName(), findPetSitter.getOptions().get(0).getName());
        assertEquals(petSitter.getImages().size(), findPetSitter.getImages().size());
        assertEquals(petSitter.getImages().get(0).getImage().getName(), findPetSitter.getImages().get(0).getName());

    }
}