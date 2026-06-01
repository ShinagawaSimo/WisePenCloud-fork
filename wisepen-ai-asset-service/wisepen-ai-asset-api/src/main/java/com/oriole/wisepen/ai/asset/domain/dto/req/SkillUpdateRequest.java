package com.oriole.wisepen.ai.asset.domain.dto.req;

import com.oriole.wisepen.ai.asset.constant.SkillValidationMsg;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillUpdateRequest {
    @NotBlank(message = SkillValidationMsg.SKILL_ID_NOT_BLANK)
    private String skillId;

    private String skillName;

    private String description;
}
