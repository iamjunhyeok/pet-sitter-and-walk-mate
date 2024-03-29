package com.iamjunhyeok.petSitterAndWalker.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.iamjunhyeok.petSitterAndWalker.image.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    private final ImageRepository imageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public Image uploadImage(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String name = UUID.randomUUID() + extension;
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(image.getContentType());
        objectMetadata.setContentLength(image.getSize());
        amazonS3.putObject(bucketName, name, image.getInputStream(), objectMetadata);
        return imageRepository.save(new Image(name));
    }

    public List<Image> uploadImage(List<MultipartFile> images) {
        List<Image> list = new ArrayList<>();
        for (MultipartFile image : images) {
            try {
                list.add(uploadImage(image));
            } catch (Exception e) {
                list.stream().forEach(image1 ->
                        amazonS3.deleteObject(bucketName, image1.getName()));
                throw new RuntimeException("An error occurred while uploading images.");
            }
        }
        return list;
    }

    public void delete(Image image) {
        amazonS3.deleteObject(bucketName, image.getName());
        imageRepository.deleteById(image.getId());
    }

    public void delete(List<Image> images) {
        for (Image image : images) {
            delete(image);
        }
    }

    public List<Image> deleteImageById(List<Long> deleteImageIds) {
        if (deleteImageIds == null || deleteImageIds.isEmpty()) return Collections.emptyList();
        List<Image> deleteImages = imageRepository.findAllById(deleteImageIds);
        for (Image deleteImage : deleteImages) {
            delete(deleteImage);
        }
        return deleteImages;
    }
}
