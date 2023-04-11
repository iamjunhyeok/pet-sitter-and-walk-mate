package com.iamjunhyeok.petSitterAndWalker.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.iamjunhyeok.petSitterAndWalker.image.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.image.repository.ImageRepository;
import com.iamjunhyeok.petSitterAndWalker.image.service.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @InjectMocks
    private S3Service s3Service;

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private ImageRepository imageRepository;

    @Test
    @DisplayName("이미지 업로드")
    void testWhenUploadValidImage() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile("files", "image1.jpg", MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{});
        Image image = new Image("image1.jpg");

        when(amazonS3.putObject(any(), any(), any(), any())).thenReturn(new PutObjectResult());
        when(imageRepository.save(any(Image.class))).thenReturn(image);

        // Act
        Image uploadImage = s3Service.uploadImage(file);

        // Assert
        verify(amazonS3, times(1)).putObject(any(), any(), any(), any());
        verify(imageRepository, times(1)).save(any(Image.class));
        assertEquals(image, uploadImage);
    }

    @Test
    @DisplayName("다수 이미지 업로드")
    void testWhenUploadValidImages() {
        // Arrange
        MultipartFile file1 = new MockMultipartFile("files", "image1.jpg", MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{});
        MultipartFile file2 = new MockMultipartFile("files", "image2.jpg", MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{});
        List<MultipartFile> files = Arrays.asList(file1, file2);

        Image image1 = new Image("image1.jpg");
        Image image2 = new Image("image2.jpg");

        when(amazonS3.putObject(any(), any(), any(), any())).thenReturn(new PutObjectResult());
        when(imageRepository.save(any(Image.class))).thenReturn(image1, image2);

        // Act
        List<Image> uploadImages = s3Service.uploadImage(files);

        // Assert
        assertEquals(files.size(), uploadImages.size());
        assertEquals(image1, uploadImages.get(0));
        assertEquals(image2, uploadImages.get(1));
        verify(amazonS3, times(2)).putObject(any(), any(), any(), any());
        verify(imageRepository, times(2)).save(any(Image.class));
    }

    @Test
    @DisplayName("이미지 삭제")
    void testDeleteImage() {
        // Arrange
        String name = "image1.jpg";
        doNothing().when(amazonS3).deleteObject(any(), any());

        // Act & Assert
//        s3Service.delete(name);
    }
}