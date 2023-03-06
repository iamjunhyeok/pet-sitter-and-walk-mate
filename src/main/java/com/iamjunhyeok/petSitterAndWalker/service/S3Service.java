package com.iamjunhyeok.petSitterAndWalker.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.iamjunhyeok.petSitterAndWalker.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
                list.stream().forEach(image1 -> amazonS3.deleteObject(bucketName, image1.getName()));
                throw new RuntimeException("An error occurred while uploading images.");
            }
        }
        return list;
    }

    public void delete(String name) {
        amazonS3.deleteObject(bucketName, name);
    }

    public void delete(List<String> names) {
        for (String name : names) {
            delete(name);
        }
    }
}
