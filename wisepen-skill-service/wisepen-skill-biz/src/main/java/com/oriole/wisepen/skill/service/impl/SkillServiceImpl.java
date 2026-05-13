package com.oriole.wisepen.skill.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.oriole.wisepen.common.core.exception.ServiceException;
import com.oriole.wisepen.skill.domain.dto.SkillCreateReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillInfoRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillUpdateReqDTO;
import com.oriole.wisepen.skill.domain.entity.SkillEntity;
import com.oriole.wisepen.skill.enums.SkillAuditStatusEnum;
import com.oriole.wisepen.skill.enums.SkillStatusEnum;
import com.oriole.wisepen.skill.exception.SkillErrorCode;
import com.oriole.wisepen.skill.repository.SkillRepository;
import com.oriole.wisepen.skill.service.ISkillService;
import com.oriole.wisepen.resource.domain.dto.ResourceCreateReqDTO;
import com.oriole.wisepen.resource.enums.ResourceType;
import com.oriole.wisepen.resource.feign.RemoteResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements ISkillService {

    private final SkillRepository skillRepository;
    private final RemoteResourceService remoteResourceService;

    @Override
    public String createSkill(SkillCreateReqDTO dto, String userId) {
        ResourceCreateReqDTO resourceReq = ResourceCreateReqDTO.builder()
                .resourceName(dto.getSkillName())
                .resourceType(ResourceType.SKILL)
                .ownerId(userId)
                .build();
        String skillId = remoteResourceService.createResource(resourceReq).getData();

        SkillEntity entity = new SkillEntity();
        entity.setSkillId(skillId);
        entity.setSkillName(dto.getSkillName());
        entity.setOwnerId(userId);
        entity.setDescription(dto.getDescription());
        entity.setSourceType(dto.getSourceType());
        entity.setSkillStatus(SkillStatusEnum.DRAFT);
        entity.setAuditStatus(SkillAuditStatusEnum.NOT_SUBMITTED);
        skillRepository.save(entity);
        return skillId;
    }

    @Override
    @Transactional
    public void deleteSkills(List<String> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return;
        }
        skillRepository.deleteBySkillIdIn(skillIds);
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
    public SkillInfoRespDTO getSkillInfo(String skillId) {
        SkillEntity entity = skillRepository.findBySkillId(skillId)
                .orElseThrow(() -> new ServiceException(SkillErrorCode.SKILL_NOT_FOUND));
        return BeanUtil.copyProperties(entity, SkillInfoRespDTO.class);
    }
}
