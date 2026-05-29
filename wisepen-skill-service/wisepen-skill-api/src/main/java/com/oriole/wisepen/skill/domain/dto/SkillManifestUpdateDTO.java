package com.oriole.wisepen.skill.domain.dto;

import com.oriole.wisepen.skill.constant.SkillValidationMsg;
import com.oriole.wisepen.skill.enums.SkillFileUpdateModeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillManifestUpdateDTO {
    @NotNull
    private SkillFileUpdateModeEnum updateMode;

    private String md5;

    private Long expectedSize;

    private String patch;

    private String baseObjectKey;

    @Builder.Default
    private Boolean createNewObject = Boolean.TRUE;
}
