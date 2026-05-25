package com.oriole.wisepen.skill.domain.dto;

import com.oriole.wisepen.skill.constant.SkillValidationMsg;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillFilesFullUpdateReqDTO {
    @NotBlank(message = SkillValidationMsg.SKILL_ID_NOT_BLANK)
    private String skillId;

    @NotBlank(message = SkillValidationMsg.SKILL_VERSION_NOT_BLANK)
    private String version;

    @Valid
    @NotNull
    private SkillManifestUpdateDTO manifest;

    @Valid
    @Builder.Default
    private List<SkillAssetUpdateItemDTO> assets = new ArrayList<>();
}
