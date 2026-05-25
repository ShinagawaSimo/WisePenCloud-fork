package com.oriole.wisepen.skill.domain.dto;

import com.oriole.wisepen.skill.enums.SkillFileUpdateModeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillManifestUpdateRespDTO {
    private SkillFileUpdateModeEnum updateMode;
    private String objectKey;
    private String putUrl;
    private String callbackHeader;
    private Boolean patchApplied;
    private Boolean flashUploaded;
}
