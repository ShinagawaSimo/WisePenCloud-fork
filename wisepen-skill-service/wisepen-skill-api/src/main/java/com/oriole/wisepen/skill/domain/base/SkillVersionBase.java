package com.oriole.wisepen.skill.domain.base;

import com.oriole.wisepen.skill.enums.SkillAuditStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SkillVersionBase {
    private String version;
    private String baseVersion;
    private String skillMdObjectKey;
    private Boolean published;
    private Boolean enabled;
    private SkillAuditStatusEnum auditStatus;
    private Boolean complete;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SkillAssetMetaBase> assetsManifest = new ArrayList<>();
}
