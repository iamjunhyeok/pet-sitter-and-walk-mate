package com.iamjunhyeok.petSitterAndWalker.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserSimpleDto {
    private Long id;
    private String name;

    public UserSimpleDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
