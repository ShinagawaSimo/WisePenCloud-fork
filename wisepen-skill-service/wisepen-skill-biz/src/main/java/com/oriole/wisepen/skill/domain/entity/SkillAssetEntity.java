package com.oriole.wisepen.skill.domain.entity;

import lombok.Data;

@Data
public class SkillAssetEntity {
    private String assetName;
    private String assetPath;
    /**
     * 核心组件类型：scripts / references / assets 等。
     */
    private String assetType;
}
