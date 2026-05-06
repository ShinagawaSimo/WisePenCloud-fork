package com.oriole.wisepen.skill.service.impl;

import com.oriole.wisepen.common.core.exception.ServiceException;
import com.oriole.wisepen.skill.config.SkillProperties;
import com.oriole.wisepen.skill.domain.dto.SkillCreateReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillInfoGetReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillInfoRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillUpdateReqDTO;
import com.oriole.wisepen.skill.domain.entity.SkillEntity;
import com.oriole.wisepen.skill.enums.SkillAuditStatusEnum;
import com.oriole.wisepen.skill.enums.SkillStatusEnum;
import com.oriole.wisepen.skill.exception.SkillErrorCode;
import com.oriole.wisepen.skill.repository.SkillRepository;
import com.oriole.wisepen.skill.service.ISkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements ISkillService {

    private final SkillRepository skillRepository;
    private final SkillProperties skillProperties;

    @Override
    public String createSkill(SkillCreateReqDTO dto) {
        String skillId = UUID.randomUUID().toString();
        SkillEntity entity = new SkillEntity();
        entity.setSkillId(skillId);
        entity.setSkillName(dto.getSkillName());
        entity.setOwnerId(dto.getOwnerId());
        entity.setDescription(dto.getDescription());
        entity.setSourceType(dto.getSourceType());
        entity.setSkillStatus(SkillStatusEnum.DRAFT);
        entity.setAuditStatus(SkillAuditStatusEnum.NOT_SUBMITTED);
        // Skill 只保存对象存储命名空间；真正的上传/下载/删除由 file-storage-service 负责。
        entity.setStorageBizTag(buildStorageBizTag(dto.getOwnerId(), skillId));
        skillRepository.save(entity);
        return skillId;
    }

    @Override
    public void updateSkill(SkillUpdateReqDTO dto) {
        SkillEntity entity = skillRepository.findBySkillId(dto.getSkillId())
                .orElseThrow(() -> new ServiceException(SkillErrorCode.SKILL_NOT_FOUND));
        if (dto.getSkillName() != null) {
            entity.setSkillName(dto.getSkillName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        skillRepository.save(entity);
    }

    @Override
    public SkillInfoRespDTO getSkillInfo(SkillInfoGetReqDTO dto) {
        SkillEntity entity = skillRepository.findBySkillId(dto.getSkillId())
                .orElseThrow(() -> new ServiceException(SkillErrorCode.SKILL_NOT_FOUND));
        SkillInfoRespDTO response = new SkillInfoRespDTO();
        response.setSkillId(entity.getSkillId());
        response.setSkillName(entity.getSkillName());
        response.setOwnerId(entity.getOwnerId());
        response.setDescription(entity.getDescription());
        response.setSkillStatus(entity.getSkillStatus());
        response.setAuditStatus(entity.getAuditStatus());
        response.setSourceType(entity.getSourceType());
        response.setStorageBizTag(entity.getStorageBizTag());
        response.setManifestObjectKey(entity.getManifestObjectKey());
        response.setScriptsObjectKey(entity.getScriptsObjectKey());
        response.setReferencesObjectKey(entity.getReferencesObjectKey());
        response.setAssetObjectKey(entity.getAssetObjectKey());
        return response;
    }

    private String buildStorageBizTag(String ownerId, String skillId) {
        if (ownerId == null || ownerId.isBlank()) {
            throw new ServiceException(SkillErrorCode.SKILL_OWNER_MISMATCH);
        }
        return skillProperties.getStorageBizTagPrefix() + "/" + ownerId + "/" + skillId;
    }
}
