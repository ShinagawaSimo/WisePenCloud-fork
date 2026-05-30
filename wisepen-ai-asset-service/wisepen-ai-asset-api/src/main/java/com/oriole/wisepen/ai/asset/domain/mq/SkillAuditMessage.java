package com.oriole.wisepen.ai.asset.domain.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillAuditMessage {
    private String skillId;
    private String ownerId;
    private String manifestPath;
}
