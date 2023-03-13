package com.iamjunhyeok.petSitterAndWalker.controller;

import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.service.PetSitterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PetSitterController {

    private final PetSitterService petSitterService;

    @PostMapping("/pet-sitter")
    public ResponseEntity<PetSitterRegisterResponse> petSitterRegister(@PathVariable Long userId,
                                                                       @RequestPart @Valid PetSitterRegisterRequest request,
                                                                       @RequestPart List<MultipartFile> files) {
        request.setImages(files);
        return new ResponseEntity<>(petSitterService.register(request, userId), HttpStatus.CREATED);
    }
}