package com.iamjunhyeok.petSitterAndWalker.controller;

import com.iamjunhyeok.petSitterAndWalker.domain.User;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetSitterInfoRegisterRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetSitterInfoUpdateRequest;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetSitterInfoUpdateResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.MyPetSitterInfoViewResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterInfoResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterListResponse;
import com.iamjunhyeok.petSitterAndWalker.dto.PetSitterRegisterResponse;
import com.iamjunhyeok.petSitterAndWalker.service.PetSitterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @GetMapping("/pet-sitter")
    public ResponseEntity<MyPetSitterInfoViewResponse> viewMyPetSitterInfo(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(petSitterService.viewMyPetSitterInfo(user), HttpStatus.OK);
    }

    @PostMapping("/pet-sitter")
    public ResponseEntity<PetSitterRegisterResponse> petSitterRegister(@RequestPart @Valid MyPetSitterInfoRegisterRequest request,
                                                                       @RequestPart List<MultipartFile> files,
                                                                       @AuthenticationPrincipal User user) {
        request.setImages(files);
        return new ResponseEntity<>(petSitterService.registerMyPetSitterInfo(request, user), HttpStatus.CREATED);
    }

    @PatchMapping("/pet-sitter")
    public ResponseEntity<MyPetSitterInfoUpdateResponse> updateMyPetSitterInfo(@RequestPart @Valid MyPetSitterInfoUpdateRequest request,
                                                                               @RequestPart List<MultipartFile> files,
                                                                               @AuthenticationPrincipal User user) {
        request.setImages(files);
        return new ResponseEntity<>(petSitterService.updateMyPetSitterInfo(request, user), HttpStatus.OK);
    }

    @GetMapping("/pet-sitters")
    public ResponseEntity<Page<PetSitterListResponse>> getPetSitters(Pageable pageable) {
        return new ResponseEntity<>(petSitterService.getPetSitters(pageable), HttpStatus.OK);
    }

    @GetMapping("/pet-sitters/{petSitterId}")
    public ResponseEntity<PetSitterInfoResponse> getPetSitter(@PathVariable Long petSitterId) {
        return new ResponseEntity<>(petSitterService.getPetSitter(petSitterId), HttpStatus.OK);
    }
}