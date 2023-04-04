package com.iamjunhyeok.petSitterAndWalker.petSitter.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iamjunhyeok.petSitterAndWalker.pet.dto.PetSimpleDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PetSitterResponse {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endDate;
    private String message;

    private List<PetSimpleDto> pets;
    private List<PetSitterOptionSimpleDto> options;
}
