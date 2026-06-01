package com.oriole.wisepen.ai.asset.domain.dto.req;

import com.oriole.wisepen.ai.asset.domain.base.SkillInfoBase;
import com.oriole.wisepen.ai.asset.domain.base.SkillVersionBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SkillInfoRequest extends SkillInfoBase {
    private String skillId;
    private String resourceId;
    private String storageBizTag;
    private SkillVersionBase currentVersionInfo;
}
