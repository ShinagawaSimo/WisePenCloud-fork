package com.oriole.wisepen.skill.service;

import com.oriole.wisepen.skill.domain.dto.SkillCreateReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillInfoRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillUpdateReqDTO;

import java.util.List;

public interface ISkillService {
    String createSkill(SkillCreateReqDTO dto, String userId);

    void deleteSkills(List<String> skillIds);

    void updateSkill(SkillUpdateReqDTO dto);

    SkillInfoRespDTO getSkillInfo(String skillId);
}
