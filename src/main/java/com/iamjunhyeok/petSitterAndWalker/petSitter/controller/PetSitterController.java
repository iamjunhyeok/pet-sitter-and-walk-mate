package com.iamjunhyeok.petSitterAndWalker.petSitter.controller;

import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterInfoResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterListResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.service.PetSitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/pet-sitters")
@RestController
public class PetSitterController {

    private final PetSitterService petSitterService;

    @GetMapping
    public ResponseEntity<Page<PetSitterListResponse>> getPetSitters(Pageable pageable) {
        return new ResponseEntity<>(petSitterService.getPetSitters(pageable), HttpStatus.OK);
    }

    @GetMapping("/{petSitterId}")
    public ResponseEntity<PetSitterInfoResponse> getPetSitter(@PathVariable Long petSitterId) {
        return new ResponseEntity<>(petSitterService.getPetSitter(petSitterId), HttpStatus.OK);
    }
}