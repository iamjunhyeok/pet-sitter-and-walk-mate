package com.iamjunhyeok.petSitterAndWalker.petSitter.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PetSitterRequestDto {
    private List<Long> petIds;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endDate;
    private List<Long> optionIds;
    private String message;

    public PetSitterRequestDto(List<Long> petIds, LocalDateTime startDate, LocalDateTime endDate, List<Long> optionIds, String message) {
        this.petIds = petIds;
        this.startDate = startDate;
        this.endDate = endDate;
        this.optionIds = optionIds;
        this.message = message;
    }
}
