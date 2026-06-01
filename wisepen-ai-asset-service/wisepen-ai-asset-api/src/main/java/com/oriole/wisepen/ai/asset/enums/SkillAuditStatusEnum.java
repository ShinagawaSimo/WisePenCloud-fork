package com.oriole.wisepen.ai.asset.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum SkillAuditStatusEnum {
    NOT_SUBMITTED("NOT_SUBMITTED"),
    PENDING("PENDING"),
    PASSED("PASSED"),
    REJECTED("REJECTED");

    @JsonValue
    private final String code;

    @JsonCreator
    public static SkillAuditStatusEnum fromCode(String code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }
}
