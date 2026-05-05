package com.oriole.wisepen.skill.domain.dto;

import com.oriole.wisepen.skill.constant.SkillValidationMsg;
import com.oriole.wisepen.skill.enums.SkillSourceTypeEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillCreateReqDTO {
    @NotBlank(message = SkillValidationMsg.SKILL_NAME_NOT_BLANK)
    private String skillName;

    @NotBlank(message = SkillValidationMsg.OWNER_ID_NOT_BLANK)
    private String ownerId;

    private String description;

    @Builder.Default
    private SkillSourceTypeEnum sourceType = SkillSourceTypeEnum.MANUAL_CREATE;
}
