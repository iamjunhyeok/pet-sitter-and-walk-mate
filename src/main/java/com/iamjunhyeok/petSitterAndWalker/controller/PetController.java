package com.iamjunhyeok.petSitterAndWalker.controller;

import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetAddRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetAddResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetViewResponse;
import com.iamjunhyeok.petSitterAndWalker.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<List<MyPetViewResponse>> viewMyPets(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(petService.viewMyPets(user), HttpStatus.OK);
    }

    @PostMapping("/my-pets")
    public ResponseEntity<MyPetAddResponse> addMyPet(@RequestPart @Valid MyPetAddRequest request,
                                                     @RequestPart List<MultipartFile> files,
                                                     @AuthenticationPrincipal User user) {
        request.setImages(files);
        return new ResponseEntity<>(petService.addMyPet(request, user), HttpStatus.CREATED);
    }
}
