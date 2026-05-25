package com.oriole.wisepen.skill.domain.entity;

import com.oriole.wisepen.skill.domain.base.SkillInfoBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "wisepen_skill_items")
public class SkillEntity extends SkillInfoBase {
    @Id
    private String skillId;

    private String resourceId;

    private String storageBizTag;

    private SkillVersionEntity currentVersionInfo;

    private String currentPublishedVersion;

    private String currentDraftVersion;

    private String draftBaseVersion;

    private List<SkillVersionEntity> versions = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createTime;

    @LastModifiedDate
    private LocalDateTime updateTime;
}
