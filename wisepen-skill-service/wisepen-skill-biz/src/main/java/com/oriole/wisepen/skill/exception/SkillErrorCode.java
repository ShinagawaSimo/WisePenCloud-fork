package com.oriole.wisepen.skill.exception;

import com.oriole.wisepen.common.core.exception.IErrorCode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SkillErrorCode implements IErrorCode {
    SKILL_NOT_FOUND(56001, "Skill 不存在"),
    SKILL_OWNER_MISMATCH(56002, "当前用户不是 Skill 所有者"),
    SKILL_VERSION_INVALID(56003, "Skill 版本不合法"),
    SKILL_RELATIVE_PATH_INVALID(56004, "Skill 相对路径不合法");

    private final Integer code;
    private final String msg;

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
