package com.oriole.wisepen.skill.domain.base;

import com.oriole.wisepen.skill.enums.SkillAuditStatusEnum;
import com.oriole.wisepen.skill.enums.SkillSourceTypeEnum;
import com.oriole.wisepen.skill.enums.SkillStatusEnum;
import lombok.Data;

@Data
public class SkillInfoBase {
    private String skillName;
    private String ownerId;
    private String description;
    private SkillStatusEnum skillStatus;
    private SkillAuditStatusEnum auditStatus;
    private SkillSourceTypeEnum sourceType;
}
