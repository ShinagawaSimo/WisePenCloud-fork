package com.oriole.wisepen.ai.asset.domain.dto.req;

import com.oriole.wisepen.ai.asset.constant.SkillValidationMsg;
import com.oriole.wisepen.ai.asset.enums.SkillSourceTypeEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillCreateRequest {
    @NotBlank(message = SkillValidationMsg.SKILL_NAME_NOT_BLANK)
    private String skillName;

    @NotBlank(message = SkillValidationMsg.OWNER_ID_NOT_BLANK)
    private String ownerId;

    private String description;

    @Builder.Default
    private SkillSourceTypeEnum sourceType = SkillSourceTypeEnum.MANUAL_CREATE;
}
