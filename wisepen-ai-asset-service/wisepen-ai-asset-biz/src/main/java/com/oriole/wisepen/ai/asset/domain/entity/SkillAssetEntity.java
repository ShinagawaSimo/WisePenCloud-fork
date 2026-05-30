package com.oriole.wisepen.ai.asset.domain.entity;

import lombok.Data;

@Data
public class SkillAssetEntity {
    private String path;
    private String objectKey;
    private String kind;
    private Long sizeBytes;
}
