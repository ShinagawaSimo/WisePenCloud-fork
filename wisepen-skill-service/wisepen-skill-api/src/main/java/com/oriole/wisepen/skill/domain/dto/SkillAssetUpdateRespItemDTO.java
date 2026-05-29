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
public class SkillAssetUpdateRespItemDTO {
    private String relativePath;
    private SkillFileUpdateModeEnum updateMode;
    private String objectKey;
    private String putUrl;
    private String callbackHeader;
    private String kind;
    private Boolean patchApplied;
    private Boolean flashUploaded;
}
