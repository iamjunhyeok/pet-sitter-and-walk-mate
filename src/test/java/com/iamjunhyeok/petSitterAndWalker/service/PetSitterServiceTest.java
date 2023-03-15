package com.iamjunhyeok.petSitterAndWalker.service;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.PetPropertyEnum;
import com.iamjunhyeok.petSitterAndWalker.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.domain.PetProperty;
import com.iamjunhyeok.petSitterAndWalker.domain.PetSitter;
import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterOptionRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetSitterInfoRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.repository.PetPropertyRepository;
import com.iamjunhyeok.petSitterAndWalker.repository.PetSitterRepository;
import com.iamjunhyeok.petSitterAndWalker.repository.UserRepository;
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

    @Test
    @DisplayName("펫 시터 정보를 등록")
    void testWhenRegisterPetSitterInfo() {
        // Arrange
        User user = mock(User.class);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        List<Long> petTypeIds = Arrays.asList(1L);
        List<Long> petSizeIds = Arrays.asList(2L);
        List<PetSitterOptionRequest> options = Arrays.asList(new PetSitterOptionRequest("목욕", "목욕시킨다", 1000));
        List<MultipartFile> files = Collections.singletonList(new MockMultipartFile("files", "image1.jpg", MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{}));
        MyPetSitterInfoRegisterRequest request = MyPetSitterInfoRegisterRequest.builder()
                .introduction("인트로")
                .petTypeIds(petTypeIds)
                .petSizeIds(petSizeIds)
                .options(options)
                .images(files)
                .build();

        PetSitter petSitter = mock(PetSitter.class);
        when(petSitterRepository.save(any())).thenReturn(petSitter);
        List<PetProperty> petTypes = Arrays.asList(new PetProperty(PetPropertyEnum.TYPE, "강아지"));
        List<PetProperty> petSizes = Arrays.asList(new PetProperty(PetPropertyEnum.SIZE, "작은 0~7kg"));
        when(petPropertyRepository.findAllById(request.getPetTypeIds())).thenReturn(petTypes);
        when(petPropertyRepository.findAllById(request.getPetSizeIds())).thenReturn(petSizes);
        List<Image> images = Arrays.asList(new Image("image1.jpg"));
        when(s3Service.uploadImage(request.getImages())).thenReturn(images);

        // Act
        PetSitterRegisterResponse response = petSitterService.registerMyPetSitterInfo(request, any());

        // Assert
        assertNotNull(response);
        assertEquals(request.getImages().size(), response.getImages().size());
        assertEquals(request.getIntroduction(), response.getIntroduction());
        assertEquals(request.getPetTypeIds().size(), response.getPetTypes().size());
        assertEquals(request.getPetSizeIds().size(), response.getPetSizes().size());
        assertEquals(request.getOptions().size(), response.getOptions().size());

        verify(petSitter, times(1)).addOption(anyList());
        verify(petSitter, times(1)).addPetType(petTypes);
        verify(petSitter, times(1)).addPetSize(petSizes);
        verify(petSitter, times(1)).addImage(images);
        verify(user, times(1)).registerPetSitterInfo(petSitter);
    }
}