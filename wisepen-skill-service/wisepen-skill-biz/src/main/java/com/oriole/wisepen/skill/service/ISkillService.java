package com.oriole.wisepen.skill.service;

import com.oriole.wisepen.skill.domain.dto.SkillCreateReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillInfoGetReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillInfoRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillUpdateReqDTO;

public interface ISkillService {
    String createSkill(SkillCreateReqDTO dto);

    void updateSkill(SkillUpdateReqDTO dto);

    SkillInfoRespDTO getSkillInfo(SkillInfoGetReqDTO dto);
}
