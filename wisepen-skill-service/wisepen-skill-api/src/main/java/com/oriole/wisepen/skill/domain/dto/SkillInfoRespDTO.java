package com.oriole.wisepen.skill.domain.dto;

import com.oriole.wisepen.skill.domain.base.SkillInfoBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SkillInfoRespDTO extends SkillInfoBase {
    private String skillId;
    private String manifestPath;
    private String assetRootPath;
}
