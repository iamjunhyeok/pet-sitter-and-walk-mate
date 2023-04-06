package com.iamjunhyeok.petSitterAndWalker.petSitter.controller;

import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.MyPetSitterInfoRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.MyPetSitterInfoUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.MyPetSitterInfoUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.MyPetSitterInfoViewResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.service.PetSitterProfileService;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/pet-sitter")
@RestController
public class PetSitterProfileController {

    private final PetSitterProfileService petSitterProfileService;

    @GetMapping
    public ResponseEntity<MyPetSitterInfoViewResponse> viewMyPetSitterInfo(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(petSitterProfileService.viewMyPetSitterInfo(user), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PetSitterRegisterResponse> petSitterRegister(@RequestPart @Valid MyPetSitterInfoRegisterRequest request,
                                                                       @RequestPart List<MultipartFile> files,
                                                                       @AuthenticationPrincipal User user) {
        request.setImages(files);
        return new ResponseEntity<>(petSitterProfileService.registerMyPetSitterInfo(request, user), HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity<MyPetSitterInfoUpdateResponse> updateMyPetSitterInfo(@RequestPart @Valid MyPetSitterInfoUpdateRequest request,
                                                                               @RequestPart List<MultipartFile> files,
                                                                               @AuthenticationPrincipal User user) {
        request.setImages(files);
        return new ResponseEntity<>(petSitterProfileService.updateMyPetSitterInfo(request, user), HttpStatus.OK);
    }
}
