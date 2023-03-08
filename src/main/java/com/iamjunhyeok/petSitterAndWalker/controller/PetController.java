package com.iamjunhyeok.petSitterAndWalker.controller;

import com.iamjunhyeok.petSitterAndWalker.dto.PetRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.PetRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.PetViewResponse;
import com.iamjunhyeok.petSitterAndWalker.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/users/{userId}/pets")
@RestController
public class PetController {

    private final PetService petService;

    @PostMapping
    public ResponseEntity<PetRegisterResponse> petRegister(@PathVariable Long userId,
                                                           @RequestPart @Valid PetRegisterRequest request,
                                                           @RequestPart List<MultipartFile> files) {
        request.setImages(files);
        return new ResponseEntity<>(petService.register(userId, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PetViewResponse>> petView(@PathVariable Long userId) {
        return new ResponseEntity<>(petService.getUserPets(userId), HttpStatus.OK);
    }
}
