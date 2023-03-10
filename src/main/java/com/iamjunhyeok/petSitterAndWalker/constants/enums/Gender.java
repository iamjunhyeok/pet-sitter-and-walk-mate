package com.iamjunhyeok.petSitterAndWalker.constants.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Gender implements Enum {
    FEMALE("암컷"),
    MALE("수컷");

    private final String value;

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return value;
    }
}