package com.oriole.wisepen.skill.domain.dto;

import com.oriole.wisepen.skill.constant.SkillValidationMsg;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDraftVersionCreateReqDTO {
    @NotBlank(message = SkillValidationMsg.SKILL_ID_NOT_BLANK)
    private String skillId;

    private String baseVersion;

    @NotBlank(message = SkillValidationMsg.SKILL_NEW_VERSION_NOT_BLANK)
    private String newVersion;
}
