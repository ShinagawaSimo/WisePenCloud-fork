package com.oriole.wisepen.skill.domain.dto;

import com.oriole.wisepen.skill.domain.base.SkillVersionBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishedSkillSnapshotRespDTO {
    private String skillId;
    private String version;
    private SkillVersionBase versionInfo;
}
