package com.iamjunhyeok.petSitterAndWalker.controller;

import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetAddRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetAddResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetListResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetViewResponse;
import com.iamjunhyeok.petSitterAndWalker.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PetController {

    private final PetService petService;

    @GetMapping("/my-pets")
    public ResponseEntity<List<MyPetListResponse>> getMyPets(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(petService.getMyPets(user), HttpStatus.OK);
    }

    @PostMapping("/my-pets")
    public ResponseEntity<MyPetAddResponse> addMyPet(@RequestPart @Valid MyPetAddRequest request,
                                                     @RequestPart List<MultipartFile> files,
                                                     @AuthenticationPrincipal User user) {
        request.setImages(files);
        return new ResponseEntity<>(petService.addMyPet(request, user), HttpStatus.CREATED);
    }

    @DeleteMapping("/my-pets/{petId}")
    public ResponseEntity<Void> deleteMyPet(@PathVariable Long petId, @AuthenticationPrincipal User user) {
        petService.deleteMyPet(petId, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/my-pets/{petId}")
    public ResponseEntity<MyPetUpdateResponse> updateMyPet(@RequestPart @Valid MyPetUpdateRequest request,
                                                           @RequestPart List<MultipartFile> files,
                                                           @PathVariable Long petId,
                                                           @AuthenticationPrincipal User user) {
        request.setImages(files);
        return new ResponseEntity<>(petService.updateMyPet(request, petId, user), HttpStatus.OK);
    }

    @GetMapping("/my-pets/{petId}")
    public ResponseEntity<MyPetViewResponse> getMyPet(@PathVariable Long petId) {
        return new ResponseEntity<>(petService.getMyPet(petId), HttpStatus.OK);
    }
}
