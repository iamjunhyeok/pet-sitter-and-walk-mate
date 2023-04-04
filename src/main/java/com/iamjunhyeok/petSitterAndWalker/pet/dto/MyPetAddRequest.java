package com.iamjunhyeok.petSitterAndWalker.pet.dto;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.Gender;
import com.iamjunhyeok.petSitterAndWalker.validator.ValueOfEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
@Getter
public class MyPetAddRequest extends MyPetRequest {

    private Long petTypeId;

    @ValueOfEnum(enumClass = Gender.class)
    private String gender;
}
