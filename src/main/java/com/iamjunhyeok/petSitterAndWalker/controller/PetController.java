package com.iamjunhyeok.petSitterAndWalker.controller;

import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.PetRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.PetRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.PetViewResponse;
import com.iamjunhyeok.petSitterAndWalker.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PetController {

    private final PetService petService;

    @GetMapping("/my-pets")
    public ResponseEntity<List<PetViewResponse>> viewMyPets(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(petService.viewMyPets(user), HttpStatus.OK);
    }

    @PostMapping("/my-pets")
    public ResponseEntity<PetRegisterResponse> petRegister(@PathVariable Long userId,
                                                           @RequestPart @Valid PetRegisterRequest request,
                                                           @RequestPart List<MultipartFile> files) {
        request.setImages(files);
        return new ResponseEntity<>(petService.register(userId, request), HttpStatus.CREATED);
    }
}
