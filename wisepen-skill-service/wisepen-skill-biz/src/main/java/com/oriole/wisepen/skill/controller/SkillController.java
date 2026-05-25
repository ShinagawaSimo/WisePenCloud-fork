package com.oriole.wisepen.skill.controller;

import com.oriole.wisepen.common.core.context.SecurityContextHolder;
import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.common.core.domain.enums.BusinessType;
import com.oriole.wisepen.common.core.exception.ServiceException;
import com.oriole.wisepen.common.log.annotation.Log;
import com.oriole.wisepen.common.security.annotation.CheckLogin;
import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitRespDTO;
import com.oriole.wisepen.resource.domain.dto.ResourceCheckPermissionReqDTO;
import com.oriole.wisepen.resource.domain.dto.ResourceCheckPermissionResDTO;
import com.oriole.wisepen.resource.enums.ResourceAccessRole;
import com.oriole.wisepen.resource.feign.RemoteResourceService;
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
import com.oriole.wisepen.skill.exception.SkillError;
import com.oriole.wisepen.skill.service.ISkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Skill 管理", description = "Skill 主档、版本与文件更新管理")
@RestController
@RequestMapping("/skill")
@RequiredArgsConstructor
@CheckLogin
public class SkillController {

    private final ISkillService skillService;
    private final RemoteResourceService remoteResourceService;

    @Operation(summary = "创建 Skill", description = "创建一个归属于当前用户的 Skill 主档")
    @Log(title = "创建 Skill", businessType = BusinessType.INSERT)
    @PostMapping("/createSkill")
    public R<String> createSkill(@Validated @RequestBody SkillCreateReqDTO dto) {
        String userId = SecurityContextHolder.getUserId().toString();
        dto.setOwnerId(userId);
        return R.ok(skillService.createSkill(dto, userId));
    }

    @Operation(summary = "更新 Skill", description = "更新 Skill 的名称与描述")
    @Log(title = "更新 Skill", businessType = BusinessType.UPDATE)
    @PostMapping("/changeSkill")
    public R<Void> updateSkill(@Validated @RequestBody SkillUpdateReqDTO dto) {
        assertSkillOwner(dto.getSkillId());
        skillService.updateSkill(dto);
        return R.ok();
    }

    @Operation(summary = "查询 Skill 详情", description = "查询 Skill 主档与版本信息")
    @PostMapping("/getSkillInfo")
    public R<SkillInfoRespDTO> getSkillInfo(@Validated @RequestBody SkillInfoGetReqDTO dto) {
        assertSkillReadable(dto.getSkillId());
        return R.ok(skillService.getSkillInfo(dto.getSkillId()));
    }

    @Operation(summary = "初始化 SKILL.md 上传", description = "为指定 Skill 版本初始化 SKILL.md 的固定 object key 上传")
    @Log(title = "初始化 Skill Manifest 上传", businessType = BusinessType.INSERT)
    @PostMapping("/initManifestUpload")
    public R<UploadInitRespDTO> initManifestUpload(@Validated @RequestBody SkillManifestUploadInitReqDTO dto) {
        assertSkillOwner(dto.getSkillId());
        return R.ok(skillService.initManifestUpload(dto));
    }

    @Operation(summary = "初始化 Skill 资产上传", description = "为指定 Skill 版本初始化附件上传并写入固定 object key")
    @Log(title = "初始化 Skill 资产上传", businessType = BusinessType.INSERT)
    @PostMapping("/initAssetUpload")
    public R<UploadInitRespDTO> initAssetUpload(@Validated @RequestBody SkillAssetUploadInitReqDTO dto) {
        assertSkillOwner(dto.getSkillId());
        return R.ok(skillService.initAssetUpload(dto));
    }

    @PostMapping("/createDraftVersion")
    public R<String> createDraftVersion(@Validated @RequestBody SkillDraftVersionCreateReqDTO dto) {
        assertSkillOwner(dto.getSkillId());
        return R.ok(skillService.createDraftVersion(dto));
    }

    @PostMapping("/listSkillVersions")
    public R<List<SkillVersionBaseListRespDTO>> listSkillVersions(@Validated @RequestBody SkillVersionListReqDTO dto) {
        assertSkillReadable(dto.getSkillId());
        return R.ok(skillService.listSkillVersions(dto));
    }

    @PostMapping("/getCurrentDraftVersion")
    public R<SkillCurrentDraftRespDTO> getCurrentDraftVersion(@Validated @RequestBody SkillVersionListReqDTO dto) {
        assertSkillReadable(dto.getSkillId());
        return R.ok(skillService.getCurrentDraftVersion(dto));
    }

    @PostMapping("/publishSkillVersion")
    public R<Void> publishSkillVersion(@Validated @RequestBody SkillVersionPublishReqDTO dto) {
        assertSkillOwner(dto.getSkillId());
        skillService.publishSkillVersion(dto.getSkillId(), dto.getVersion());
        return R.ok();
    }

    @PostMapping("/offlineSkill")
    public R<Void> offlineSkill(@Validated @RequestBody SkillOfflineReqDTO dto) {
        assertSkillOwner(dto.getSkillId());
        skillService.offlineSkill(dto.getSkillId());
        return R.ok();
    }

    @PostMapping("/getPublishedSkillSnapshot")
    public R<PublishedSkillSnapshotRespDTO> getPublishedSkillSnapshot(@Validated @RequestBody SkillVersionListReqDTO dto) {
        assertSkillReadable(dto.getSkillId());
        return R.ok(skillService.getPublishedSkillSnapshot(dto.getSkillId()));
    }

    @PostMapping("/fullUpdateSkillFiles")
    public R<SkillFilesFullUpdateRespDTO> fullUpdateSkillFiles(@Validated @RequestBody SkillFilesFullUpdateReqDTO dto) {
        assertSkillOwner(dto.getSkillId());
        return R.ok(skillService.fullUpdateSkillFiles(dto));
    }

    @PostMapping("/updateDraftVersionFiles")
    public R<SkillFilesFullUpdateRespDTO> updateDraftVersionFiles(@Validated @RequestBody SkillFilesFullUpdateReqDTO dto) {
        assertSkillOwner(dto.getSkillId());
        return R.ok(skillService.fullUpdateSkillFiles(dto));
    }

    private void assertSkillReadable(String skillId) {
        remoteResourceService.checkResPermission(new ResourceCheckPermissionReqDTO(
                skillId, SecurityContextHolder.getUserId(), SecurityContextHolder.getGroupRoleMap()
        ));
    }

    private void assertSkillOwner(String skillId) {
        ResourceCheckPermissionResDTO permission = remoteResourceService.checkResPermission(new ResourceCheckPermissionReqDTO(
                skillId, SecurityContextHolder.getUserId(), SecurityContextHolder.getGroupRoleMap()
        )).getData();
        if (permission == null || permission.getResourceAccessRole() != ResourceAccessRole.OWNER) {
            throw new ServiceException(SkillError.SKILL_OWNER_MISMATCH);
        }
    }
}
