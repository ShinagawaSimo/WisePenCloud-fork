package com.oriole.wisepen.ai.asset.controller;

import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitRespDTO;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillAssetUploadInitRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillCreateRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillInfoGetRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillInfoRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillManifestUploadInitRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillUpdateRequest;
import com.oriole.wisepen.ai.asset.feign.RemoteSkillService;
import com.oriole.wisepen.ai.asset.service.ISkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/skill")
@RequiredArgsConstructor
public class InternalSkillController implements RemoteSkillService {

    private final ISkillService skillService;

    @Override
    @PostMapping("/createSkill")
    public R<String> createSkill(@Validated @RequestBody SkillCreateRequest dto) {
        return R.ok(skillService.createSkill(dto, dto.getOwnerId()));
    }

    @Override
    @PostMapping("/changeSkill")
    public R<Void> updateSkill(@Validated @RequestBody SkillUpdateRequest dto) {
        skillService.updateSkill(dto);
        return R.ok();
    }

    @Override
    @PostMapping("/getSkillInfo")
    public R<SkillInfoRequest> getSkillInfo(@Validated @RequestBody SkillInfoGetRequest dto) {
        return R.ok(skillService.getSkillInfo(dto.getSkillId()));
    }

    @Override
    @PostMapping("/initManifestUpload")
    public R<UploadInitRespDTO> initManifestUpload(@Validated @RequestBody SkillManifestUploadInitRequest dto) {
        return R.ok(skillService.initManifestUpload(dto));
    }

    @Override
    @PostMapping("/initAssetUpload")
    public R<UploadInitRespDTO> initAssetUpload(@Validated @RequestBody SkillAssetUploadInitRequest dto) {
        return R.ok(skillService.initAssetUpload(dto));
    }
}
