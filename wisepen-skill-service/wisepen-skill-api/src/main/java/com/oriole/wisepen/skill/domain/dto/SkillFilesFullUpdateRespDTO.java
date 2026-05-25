package com.oriole.wisepen.skill.domain.dto;

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
public class SkillFilesFullUpdateRespDTO {
    private String skillId;
    private String version;
    private SkillManifestUpdateRespDTO manifest;
    @Builder.Default
    private List<SkillAssetUpdateRespItemDTO> assets = new ArrayList<>();
    @Builder.Default
    private List<String> removedPaths = new ArrayList<>();
}
