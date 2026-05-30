package com.oriole.wisepen.ai.asset.domain.base;

import com.oriole.wisepen.ai.asset.enums.SkillAuditStatusEnum;
import com.oriole.wisepen.ai.asset.enums.SkillSourceTypeEnum;
import com.oriole.wisepen.ai.asset.enums.SkillStatusEnum;
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
