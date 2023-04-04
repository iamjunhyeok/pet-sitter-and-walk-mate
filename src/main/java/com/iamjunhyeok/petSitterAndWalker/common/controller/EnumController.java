package com.iamjunhyeok.petSitterAndWalker.common.controller;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.Enum;
import com.iamjunhyeok.petSitterAndWalker.constants.enums.Gender;
import com.iamjunhyeok.petSitterAndWalker.common.dto.EnumDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class EnumController {

    @GetMapping("/genders")
    public List<EnumDto> genders() {
        return toDtoList(Gender.class);
    }

    public List<EnumDto> toDtoList(Class<? extends Enum> enums) {
        List<EnumDto> list = new ArrayList<>();
        for (Enum enumConstant : enums.getEnumConstants()) {
            list.add(new EnumDto(enumConstant.getKey(), enumConstant.getValue()));
        }
        return list;
    }
}
