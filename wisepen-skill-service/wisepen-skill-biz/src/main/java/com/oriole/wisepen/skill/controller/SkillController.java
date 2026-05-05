package com.oriole.wisepen.skill.controller;

import com.oriole.wisepen.common.core.context.SecurityContextHolder;
import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.common.core.domain.enums.BusinessType;
import com.oriole.wisepen.common.log.annotation.Log;
import com.oriole.wisepen.common.security.annotation.CheckLogin;
import com.oriole.wisepen.skill.domain.dto.SkillCreateReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillInfoGetReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillInfoRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillUpdateReqDTO;
import com.oriole.wisepen.skill.service.ISkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Skill 管理", description = "Skill 主档的创建、更新与查询")
@RestController
@RequestMapping("/skill")
@RequiredArgsConstructor
@CheckLogin
public class SkillController {

    private final ISkillService skillService;

    @Operation(summary = "创建 Skill", description = "创建一个归属于当前用户的 Skill 主档")
    @Log(title = "创建 Skill", businessType = BusinessType.INSERT)
    @PostMapping("/createSkill")
    public R<String> createSkill(@Validated @RequestBody SkillCreateReqDTO dto) {
        dto.setOwnerId(SecurityContextHolder.getUserId().toString());
        return R.ok(skillService.createSkill(dto));
    }

    @Operation(summary = "更新 Skill", description = "更新 Skill 的名称与描述")
    @Log(title = "更新 Skill", businessType = BusinessType.UPDATE)
    @PostMapping("/changeSkill")
    public R<Void> updateSkill(@Validated @RequestBody SkillUpdateReqDTO dto) {
        skillService.updateSkill(dto);
        return R.ok();
    }

    @Operation(summary = "查询 Skill 详情", description = "查询 Skill 主档与核心目录信息")
    @PostMapping("/getSkillInfo")
    public R<SkillInfoRespDTO> getSkillInfo(@Validated @RequestBody SkillInfoGetReqDTO dto) {
        return R.ok(skillService.getSkillInfo(dto));
    }
}
