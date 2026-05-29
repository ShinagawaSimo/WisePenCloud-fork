package com.oriole.wisepen.skill.domain.dto;

import com.oriole.wisepen.skill.domain.base.SkillInfoBase;
import com.oriole.wisepen.skill.domain.base.SkillVersionBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SkillInfoRespDTO extends SkillInfoBase {
    private String skillId;
    private String resourceId;
    private String storageBizTag;
    private String currentPublishedVersion;
    private String currentDraftVersion;
    private String draftBaseVersion;
    private SkillVersionBase currentVersionInfo;
    private SkillVersionBase currentPublishedVersionInfo;
    private SkillVersionBase currentDraftVersionInfo;
    private List<SkillVersionBase> versions = new ArrayList<>();
}
