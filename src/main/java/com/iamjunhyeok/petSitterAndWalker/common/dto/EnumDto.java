package com.iamjunhyeok.petSitterAndWalker.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnumDto {
    private String key;
    private String value;

    public EnumDto(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
