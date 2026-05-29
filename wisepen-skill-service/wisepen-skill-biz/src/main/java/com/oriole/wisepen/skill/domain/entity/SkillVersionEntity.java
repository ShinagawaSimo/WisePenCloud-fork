package com.oriole.wisepen.skill.domain.entity;

import com.oriole.wisepen.skill.enums.SkillAuditStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SkillVersionEntity {
    private String version;
    private String baseVersion;
    private String skillMdObjectKey;
    private Boolean published = false;
    private Boolean enabled = false;
    private SkillAuditStatusEnum auditStatus = SkillAuditStatusEnum.NOT_SUBMITTED;
    private Boolean complete = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SkillAssetEntity> assetsManifest = new ArrayList<>();
}
