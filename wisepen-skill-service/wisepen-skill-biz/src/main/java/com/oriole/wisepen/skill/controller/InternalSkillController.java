package com.oriole.wisepen.skill.controller;

import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitRespDTO;
import com.oriole.wisepen.skill.domain.dto.PublishedSkillSnapshotRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillAssetUploadInitReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillCreateReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillCurrentDraftRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillDraftVersionCreateReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillFilesFullUpdateReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillFilesFullUpdateRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillInfoGetReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillInfoRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillManifestUploadInitReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillOfflineReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillUpdateReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillVersionBaseListRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillVersionListReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillVersionPublishReqDTO;
import com.oriole.wisepen.skill.feign.RemoteSkillService;
import com.oriole.wisepen.skill.service.ISkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/skill")
@RequiredArgsConstructor
public class InternalSkillController implements RemoteSkillService {

    private final ISkillService skillService;

    @Override
    @PostMapping("/createSkill")
    public R<String> createSkill(@Validated @RequestBody SkillCreateReqDTO dto) {
        return R.ok(skillService.createSkill(dto, dto.getOwnerId()));
    }

    @Override
    @PostMapping("/changeSkill")
    public R<Void> updateSkill(@Validated @RequestBody SkillUpdateReqDTO dto) {
        skillService.updateSkill(dto);
        return R.ok();
    }

    @Override
    @PostMapping("/getSkillInfo")
    public R<SkillInfoRespDTO> getSkillInfo(@Validated @RequestBody SkillInfoGetReqDTO dto) {
        return R.ok(skillService.getSkillInfo(dto.getSkillId()));
    }

    @Override
    @PostMapping("/initManifestUpload")
    public R<UploadInitRespDTO> initManifestUpload(@Validated @RequestBody SkillManifestUploadInitReqDTO dto) {
        return R.ok(skillService.initManifestUpload(dto));
    }

    @Override
    @PostMapping("/initAssetUpload")
    public R<UploadInitRespDTO> initAssetUpload(@Validated @RequestBody SkillAssetUploadInitReqDTO dto) {
        return R.ok(skillService.initAssetUpload(dto));
    }

    @Override
    @PostMapping("/createDraftVersion")
    public R<String> createDraftVersion(@Validated @RequestBody SkillDraftVersionCreateReqDTO dto) {
        return R.ok(skillService.createDraftVersion(dto));
    }

    @Override
    @PostMapping("/listSkillVersions")
    public R<List<SkillVersionBaseListRespDTO>> listSkillVersions(@Validated @RequestBody SkillVersionListReqDTO dto) {
        return R.ok(skillService.listSkillVersions(dto));
    }

    @Override
    @PostMapping("/getCurrentDraftVersion")
    public R<SkillCurrentDraftRespDTO> getCurrentDraftVersion(@Validated @RequestBody SkillVersionListReqDTO dto) {
        return R.ok(skillService.getCurrentDraftVersion(dto));
    }

    @Override
    @PostMapping("/publishSkillVersion")
    public R<Void> publishSkillVersion(@Validated @RequestBody SkillVersionPublishReqDTO dto) {
        skillService.publishSkillVersion(dto.getSkillId(), dto.getVersion());
        return R.ok();
    }

    @Override
    @PostMapping("/offlineSkill")
    public R<Void> offlineSkill(@Validated @RequestBody SkillOfflineReqDTO dto) {
        skillService.offlineSkill(dto.getSkillId());
        return R.ok();
    }

    @Override
    @PostMapping("/getPublishedSkillSnapshot")
    public R<PublishedSkillSnapshotRespDTO> getPublishedSkillSnapshot(@Validated @RequestBody SkillVersionListReqDTO dto) {
        return R.ok(skillService.getPublishedSkillSnapshot(dto.getSkillId()));
    }

    @Override
    @PostMapping("/fullUpdateSkillFiles")
    public R<SkillFilesFullUpdateRespDTO> fullUpdateSkillFiles(@Validated @RequestBody SkillFilesFullUpdateReqDTO dto) {
        return R.ok(skillService.fullUpdateSkillFiles(dto));
    }
}
