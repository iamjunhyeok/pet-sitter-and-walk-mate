package com.iamjunhyeok.petSitterAndWalker.petSitter.controller;

import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterRequestDto;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.service.PetSitterRequestService;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/pet-sitters/{petSitterId}/requests")
@RestController
public class PetSitterRequestController {

    private final PetSitterRequestService petSitterRequestService;

    @PostMapping
    public ResponseEntity<PetSitterResponse> requestToPetSitter(@RequestBody @Valid PetSitterRequestDto request,
                                                              @PathVariable Long petSitterId,
                                                              @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(petSitterRequestService.requestToPetSitter(request, petSitterId, user), HttpStatus.CREATED);
    }

    @PutMapping("/{requestId}/accept")
    public ResponseEntity<Void> acceptRequest(@PathVariable Long petSitterId, @PathVariable Long requestId, @AuthenticationPrincipal User user) {
        petSitterRequestService.acceptRequest(petSitterId, requestId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{requestId}/reject")
    public ResponseEntity<Void> rejectRequest(@PathVariable Long petSitterId, @PathVariable Long requestId, @AuthenticationPrincipal User user) {
        petSitterRequestService.rejectRequest(petSitterId, requestId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{requestId}/cancel")
    public ResponseEntity<Void> cancelRequest(@PathVariable Long petSitterId, @PathVariable Long requestId, @AuthenticationPrincipal User user) {
        petSitterRequestService.cancelRequest(petSitterId, requestId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
