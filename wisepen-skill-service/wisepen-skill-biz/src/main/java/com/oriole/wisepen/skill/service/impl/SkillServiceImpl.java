package com.oriole.wisepen.skill.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.oriole.wisepen.common.core.exception.ServiceException;
import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitRespDTO;
import com.oriole.wisepen.skill.domain.base.SkillAssetMetaBase;
import com.oriole.wisepen.skill.domain.base.SkillVersionBase;
import com.oriole.wisepen.skill.domain.dto.PublishedSkillSnapshotRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillAssetUpdateItemDTO;
import com.oriole.wisepen.skill.domain.dto.SkillAssetUpdateRespItemDTO;
import com.oriole.wisepen.skill.domain.dto.SkillAssetUploadInitReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillCreateReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillCurrentDraftRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillDraftVersionCreateReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillFilesFullUpdateReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillFilesFullUpdateRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillInfoRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillManifestUpdateRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillManifestUploadInitReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillUpdateReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillVersionBaseListRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillVersionListReqDTO;
import com.oriole.wisepen.skill.domain.entity.SkillAssetEntity;
import com.oriole.wisepen.skill.domain.entity.SkillEntity;
import com.oriole.wisepen.skill.domain.entity.SkillVersionEntity;
import com.oriole.wisepen.skill.enums.SkillAuditStatusEnum;
import com.oriole.wisepen.skill.enums.SkillFileUpdateModeEnum;
import com.oriole.wisepen.skill.enums.SkillStatusEnum;
import com.oriole.wisepen.skill.exception.SkillError;
import com.oriole.wisepen.skill.repository.SkillRepository;
import com.oriole.wisepen.skill.service.ISkillService;
import com.oriole.wisepen.skill.service.ISkillStorageService;
import com.oriole.wisepen.resource.domain.dto.ResourceCreateReqDTO;
import com.oriole.wisepen.resource.enums.ResourceType;
import com.oriole.wisepen.resource.feign.RemoteResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements ISkillService {

    private static final String MANIFEST_PATH = "SKILL.md";

    private final SkillRepository skillRepository;
    private final RemoteResourceService remoteResourceService;
    private final ISkillStorageService skillStorageService;

    @Override
    public String createSkill(SkillCreateReqDTO dto, String userId) {
        ResourceCreateReqDTO resourceReq = ResourceCreateReqDTO.builder()
                .resourceName(dto.getSkillName())
                .resourceType(ResourceType.SKILL)
                .ownerId(userId)
                .build();
        String skillId = remoteResourceService.createResource(resourceReq).getData();
        if (!StringUtils.hasText(skillId)) {
            throw new ServiceException(SkillError.SKILL_RESOURCE_REGISTER_FAILED);
        }

        SkillEntity entity = new SkillEntity();
        entity.setSkillId(skillId);
        entity.setResourceId(skillId);
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
        SkillEntity entity = getSkill(dto.getSkillId());
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
        SkillEntity entity = syncLegacyVersionFields(getSkill(skillId));
        SkillInfoRespDTO response = BeanUtil.copyProperties(entity, SkillInfoRespDTO.class);
        response.setVersions(entity.getVersions().stream().map(this::toVersionBase).toList());
        response.setCurrentVersionInfo(findVersionBase(entity, entity.getCurrentDraftVersion()));
        response.setCurrentDraftVersionInfo(findVersionBase(entity, entity.getCurrentDraftVersion()));
        response.setCurrentPublishedVersionInfo(findVersionBase(entity, entity.getCurrentPublishedVersion()));
        return response;
    }

    @Override
    public UploadInitRespDTO initManifestUpload(SkillManifestUploadInitReqDTO dto) {
        validateVersion(dto.getVersion());
        SkillEntity entity = syncLegacyVersionFields(getSkill(dto.getSkillId()));

        UploadInitRespDTO response = skillStorageService.initManifestUpload(
                dto.getSkillId(), dto.getVersion(), dto.getMd5(), dto.getExpectedSize()
        );

        SkillVersionEntity versionInfo = getOrCreateVersion(entity, dto.getVersion(), null);
        versionInfo.setSkillMdObjectKey(response.getObjectKey());
        versionInfo.setUpdatedAt(LocalDateTime.now());
        refreshVersionComplete(versionInfo);
        touchDraft(entity, versionInfo);
        skillRepository.save(entity);
        return response;
    }

    @Override
    public UploadInitRespDTO initAssetUpload(SkillAssetUploadInitReqDTO dto) {
        validateVersion(dto.getVersion());
        validateRelativePath(dto.getRelativePath());
        SkillEntity entity = syncLegacyVersionFields(getSkill(dto.getSkillId()));

        UploadInitRespDTO response = skillStorageService.initAssetUpload(
                dto.getSkillId(), dto.getVersion(), dto.getRelativePath(), dto.getMd5(), dto.getExpectedSize()
        );

        SkillVersionEntity versionInfo = getOrCreateVersion(entity, dto.getVersion(), null);
        SkillAssetEntity asset = findOrCreateAsset(versionInfo, dto.getRelativePath());
        asset.setObjectKey(response.getObjectKey());
        asset.setKind(dto.getKind());
        asset.setSizeBytes(dto.getExpectedSize());
        versionInfo.setUpdatedAt(LocalDateTime.now());
        refreshVersionComplete(versionInfo);
        touchDraft(entity, versionInfo);
        skillRepository.save(entity);
        return response;
    }

    @Override
    public String createDraftVersion(SkillDraftVersionCreateReqDTO dto) {
        validateVersion(dto.getNewVersion());
        SkillEntity entity = syncLegacyVersionFields(getSkill(dto.getSkillId()));
        if (findVersion(entity, dto.getNewVersion()) != null) {
            throw new ServiceException(SkillError.SKILL_VERSION_ALREADY_EXISTS);
        }

        SkillVersionEntity version = new SkillVersionEntity();
        version.setVersion(dto.getNewVersion());
        version.setBaseVersion(dto.getBaseVersion());
        version.setAuditStatus(SkillAuditStatusEnum.NOT_SUBMITTED);
        version.setPublished(false);
        version.setEnabled(false);
        version.setCreatedAt(LocalDateTime.now());
        version.setUpdatedAt(LocalDateTime.now());

        if (StringUtils.hasText(dto.getBaseVersion())) {
            SkillVersionEntity baseVersion = requireVersion(entity, dto.getBaseVersion());
            version.setSkillMdObjectKey(baseVersion.getSkillMdObjectKey());
            version.setAssetsManifest(copyAssets(baseVersion.getAssetsManifest()));
            version.setComplete(Boolean.TRUE.equals(baseVersion.getComplete()));
        }

        entity.getVersions().add(version);
        entity.setCurrentDraftVersion(version.getVersion());
        entity.setDraftBaseVersion(version.getBaseVersion());
        entity.setCurrentVersionInfo(version);
        skillRepository.save(entity);
        return version.getVersion();
    }

    @Override
    public List<SkillVersionBaseListRespDTO> listSkillVersions(SkillVersionListReqDTO dto) {
        SkillEntity entity = syncLegacyVersionFields(getSkill(dto.getSkillId()));
        return entity.getVersions().stream().map(version -> BeanUtil.copyProperties(toVersionBase(version), SkillVersionBaseListRespDTO.class)).toList();
    }

    @Override
    public SkillCurrentDraftRespDTO getCurrentDraftVersion(SkillVersionListReqDTO dto) {
        SkillEntity entity = syncLegacyVersionFields(getSkill(dto.getSkillId()));
        return SkillCurrentDraftRespDTO.builder()
                .skillId(entity.getSkillId())
                .currentDraftVersion(entity.getCurrentDraftVersion())
                .draftBaseVersion(entity.getDraftBaseVersion())
                .currentDraftVersionInfo(findVersionBase(entity, entity.getCurrentDraftVersion()))
                .build();
    }

    @Override
    public void publishSkillVersion(String skillId, String version) {
        SkillEntity entity = syncLegacyVersionFields(getSkill(skillId));
        SkillVersionEntity target = requireVersion(entity, version);
        if (!Boolean.TRUE.equals(target.getComplete())) {
            throw new ServiceException(SkillError.SKILL_VERSION_NOT_COMPLETE);
        }
        for (SkillVersionEntity item : entity.getVersions()) {
            item.setPublished(Objects.equals(item.getVersion(), version));
            item.setEnabled(Objects.equals(item.getVersion(), version));
        }
        entity.setCurrentPublishedVersion(version);
        entity.setSkillStatus(SkillStatusEnum.ACTIVE);
        skillRepository.save(entity);
    }

    @Override
    public void offlineSkill(String skillId) {
        SkillEntity entity = syncLegacyVersionFields(getSkill(skillId));
        entity.setCurrentPublishedVersion(null);
        for (SkillVersionEntity item : entity.getVersions()) {
            item.setPublished(false);
            item.setEnabled(false);
        }
        entity.setSkillStatus(SkillStatusEnum.OFFLINE);
        skillRepository.save(entity);
    }

    @Override
    public PublishedSkillSnapshotRespDTO getPublishedSkillSnapshot(String skillId) {
        SkillEntity entity = syncLegacyVersionFields(getSkill(skillId));
        SkillVersionEntity version = findVersion(entity, entity.getCurrentPublishedVersion());
        if (version == null) {
            throw new ServiceException(SkillError.SKILL_PUBLISHED_VERSION_NOT_FOUND);
        }
        return PublishedSkillSnapshotRespDTO.builder()
                .skillId(skillId)
                .version(version.getVersion())
                .versionInfo(toVersionBase(version))
                .build();
    }

    @Override
    @Transactional
    public SkillFilesFullUpdateRespDTO fullUpdateSkillFiles(SkillFilesFullUpdateReqDTO dto) {
        validateVersion(dto.getVersion());
        SkillEntity entity = syncLegacyVersionFields(getSkill(dto.getSkillId()));
        SkillVersionEntity version = requireVersion(entity, dto.getVersion());
        if (!Objects.equals(entity.getCurrentDraftVersion(), dto.getVersion())) {
            throw new ServiceException(SkillError.SKILL_DRAFT_VERSION_REQUIRED);
        }

        validateManifestPatch(dto);
        validateAssetUpdateItems(dto.getAssets());

        SkillFilesFullUpdateRespDTO response = SkillFilesFullUpdateRespDTO.builder()
                .skillId(dto.getSkillId())
                .version(dto.getVersion())
                .build();

        Map<String, SkillAssetEntity> previousAssets = new LinkedHashMap<>();
        for (SkillAssetEntity asset : version.getAssetsManifest()) {
            previousAssets.put(asset.getPath(), asset);
        }

        SkillVersionEntity updated = copyVersion(version);
        updated.setUpdatedAt(LocalDateTime.now());

        response.setManifest(updateManifest(dto, updated));
        List<SkillAssetEntity> newAssets = new ArrayList<>();
        List<SkillAssetUpdateRespItemDTO> assetResponses = new ArrayList<>();
        Set<String> touchedPaths = new LinkedHashSet<>();

        for (SkillAssetUpdateItemDTO item : dto.getAssets()) {
            touchedPaths.add(item.getRelativePath());
            SkillAssetEntity previous = previousAssets.get(item.getRelativePath());
            if (item.getUpdateMode() == SkillFileUpdateModeEnum.PATCH) {
                String baseObjectKey = resolveBaseObjectKey(item.getBaseObjectKey(), previous, updated, item.getRelativePath());
                String content = applyPatch(skillStorageService.readBaseText(baseObjectKey), item.getPatch());
                UploadInitRespDTO upload = skillStorageService.applyTextPatchAndWriteObject(
                        buildObjectKey(dto.getSkillId(), dto.getVersion(), item.getRelativePath()),
                        extractExtension(item.getRelativePath()),
                        content
                );
                newAssets.add(buildAsset(item, upload.getObjectKey(), content.length()));
                assetResponses.add(buildAssetResp(item, upload, true));
            } else {
                UploadInitRespDTO upload = skillStorageService.initAssetUpload(
                        dto.getSkillId(), dto.getVersion(), item.getRelativePath(), item.getMd5(), item.getExpectedSize()
                );
                newAssets.add(buildAsset(item, upload.getObjectKey(), item.getExpectedSize()));
                assetResponses.add(buildAssetResp(item, upload, false));
            }
        }

        updated.setAssetsManifest(newAssets);
        updated.setComplete(StringUtils.hasText(updated.getSkillMdObjectKey()));
        replaceVersion(entity, updated);
        touchDraft(entity, updated);

        List<String> removedPaths = previousAssets.keySet().stream()
                .filter(path -> !touchedPaths.contains(path))
                .toList();
        response.setRemovedPaths(new ArrayList<>(removedPaths));
        response.setAssets(assetResponses);

        skillRepository.save(entity);
        return response;
    }

    private SkillManifestUpdateRespDTO updateManifest(SkillFilesFullUpdateReqDTO dto, SkillVersionEntity updated) {
        if (dto.getManifest().getUpdateMode() == SkillFileUpdateModeEnum.PATCH) {
            String baseObjectKey = resolveBaseObjectKey(dto.getManifest().getBaseObjectKey(), null, updated, MANIFEST_PATH);
            String content = applyPatch(skillStorageService.readBaseText(baseObjectKey), dto.getManifest().getPatch());
            UploadInitRespDTO upload = skillStorageService.applyTextPatchAndWriteObject(
                    buildObjectKey(dto.getSkillId(), dto.getVersion(), MANIFEST_PATH),
                    "md",
                    content
            );
            updated.setSkillMdObjectKey(upload.getObjectKey());
            return SkillManifestUpdateRespDTO.builder()
                    .updateMode(dto.getManifest().getUpdateMode())
                    .objectKey(upload.getObjectKey())
                    .putUrl(upload.getPutUrl())
                    .callbackHeader(upload.getCallbackHeader())
                    .flashUploaded(upload.getFlashUploaded())
                    .patchApplied(true)
                    .build();
        }

        UploadInitRespDTO upload = skillStorageService.initManifestUpload(
                dto.getSkillId(), dto.getVersion(), dto.getManifest().getMd5(), dto.getManifest().getExpectedSize()
        );
        updated.setSkillMdObjectKey(upload.getObjectKey());
        return SkillManifestUpdateRespDTO.builder()
                .updateMode(dto.getManifest().getUpdateMode())
                .objectKey(upload.getObjectKey())
                .putUrl(upload.getPutUrl())
                .callbackHeader(upload.getCallbackHeader())
                .flashUploaded(upload.getFlashUploaded())
                .patchApplied(false)
                .build();
    }

    private SkillAssetUpdateRespItemDTO buildAssetResp(SkillAssetUpdateItemDTO item, UploadInitRespDTO upload, boolean patchApplied) {
        return SkillAssetUpdateRespItemDTO.builder()
                .relativePath(item.getRelativePath())
                .updateMode(item.getUpdateMode())
                .objectKey(upload.getObjectKey())
                .putUrl(upload.getPutUrl())
                .callbackHeader(upload.getCallbackHeader())
                .kind(item.getKind())
                .patchApplied(patchApplied)
                .flashUploaded(upload.getFlashUploaded())
                .build();
    }

    private SkillAssetEntity buildAsset(SkillAssetUpdateItemDTO item, String objectKey, Long size) {
        SkillAssetEntity asset = new SkillAssetEntity();
        asset.setPath(item.getRelativePath());
        asset.setObjectKey(objectKey);
        asset.setKind(item.getKind());
        asset.setSizeBytes(size);
        return asset;
    }

    private void validateManifestPatch(SkillFilesFullUpdateReqDTO dto) {
        if (dto.getManifest().getUpdateMode() == SkillFileUpdateModeEnum.PATCH && !StringUtils.hasText(dto.getManifest().getPatch())) {
            throw new ServiceException(SkillError.SKILL_TEXT_UPDATE_INVALID);
        }
        if (dto.getManifest().getUpdateMode() == SkillFileUpdateModeEnum.UPLOAD && !StringUtils.hasText(dto.getManifest().getMd5())) {
            throw new ServiceException(SkillError.SKILL_TEXT_UPDATE_INVALID);
        }
    }

    private void validateAssetUpdateItems(List<SkillAssetUpdateItemDTO> items) {
        Set<String> paths = new LinkedHashSet<>();
        for (SkillAssetUpdateItemDTO item : items) {
            validateRelativePath(item.getRelativePath());
            if (MANIFEST_PATH.equals(item.getRelativePath()) || !paths.add(item.getRelativePath())) {
                throw new ServiceException(SkillError.SKILL_RELATIVE_PATH_INVALID);
            }
            if (item.getUpdateMode() == SkillFileUpdateModeEnum.PATCH) {
                if (!isTextLike(item.getRelativePath()) || !StringUtils.hasText(item.getPatch())) {
                    throw new ServiceException(SkillError.SKILL_TEXT_UPDATE_INVALID);
                }
            } else if (!StringUtils.hasText(item.getMd5())) {
                throw new ServiceException(SkillError.SKILL_TEXT_UPDATE_INVALID);
            }
        }
    }

    private String resolveBaseObjectKey(String explicitBaseObjectKey, SkillAssetEntity previousAsset, SkillVersionEntity version, String path) {
        if (StringUtils.hasText(explicitBaseObjectKey)) {
            return explicitBaseObjectKey;
        }
        if (MANIFEST_PATH.equals(path)) {
            if (!StringUtils.hasText(version.getSkillMdObjectKey())) {
                throw new ServiceException(SkillError.SKILL_PATCH_INVALID);
            }
            return version.getSkillMdObjectKey();
        }
        if (previousAsset == null || !StringUtils.hasText(previousAsset.getObjectKey())) {
            throw new ServiceException(SkillError.SKILL_PATCH_INVALID);
        }
        return previousAsset.getObjectKey();
    }

    private String applyPatch(String baseContent, String patch) {
        if (!StringUtils.hasText(patch)) {
            throw new ServiceException(SkillError.SKILL_PATCH_INVALID);
        }
        if (patch.startsWith("@@") || patch.contains("\n@@")) {
            return patch;
        }
        return patch;
    }

    private boolean isTextLike(String relativePath) {
        return relativePath.endsWith(".md")
                || relativePath.endsWith(".py")
                || relativePath.endsWith(".txt")
                || relativePath.endsWith(".json")
                || relativePath.endsWith(".yaml")
                || relativePath.endsWith(".yml");
    }

    private SkillEntity getSkill(String skillId) {
        return skillRepository.findBySkillId(skillId)
                .orElseThrow(() -> new ServiceException(SkillError.SKILL_NOT_FOUND));
    }

    private SkillEntity syncLegacyVersionFields(SkillEntity entity) {
        if ((entity.getVersions() == null || entity.getVersions().isEmpty()) && entity.getCurrentVersionInfo() != null) {
            entity.setVersions(new ArrayList<>(List.of(entity.getCurrentVersionInfo())));
        }
        if ((entity.getCurrentDraftVersion() == null || entity.getCurrentDraftVersion().isBlank()) && entity.getCurrentVersionInfo() != null) {
            entity.setCurrentDraftVersion(entity.getCurrentVersionInfo().getVersion());
        }
        if (entity.getVersions() == null) {
            entity.setVersions(new ArrayList<>());
        }
        return entity;
    }

    private SkillVersionEntity getOrCreateVersion(SkillEntity entity, String version, String baseVersion) {
        SkillVersionEntity existing = findVersion(entity, version);
        if (existing != null) {
            return existing;
        }
        SkillVersionEntity created = new SkillVersionEntity();
        created.setVersion(version);
        created.setBaseVersion(baseVersion);
        created.setCreatedAt(LocalDateTime.now());
        created.setUpdatedAt(LocalDateTime.now());
        entity.getVersions().add(created);
        return created;
    }

    private SkillVersionEntity requireVersion(SkillEntity entity, String version) {
        SkillVersionEntity versionEntity = findVersion(entity, version);
        if (versionEntity == null) {
            throw new ServiceException(SkillError.SKILL_VERSION_NOT_FOUND);
        }
        return versionEntity;
    }

    private SkillVersionEntity findVersion(SkillEntity entity, String version) {
        if (!StringUtils.hasText(version)) {
            return null;
        }
        return entity.getVersions().stream()
                .filter(item -> version.equals(item.getVersion()))
                .findFirst()
                .orElse(null);
    }

    private void replaceVersion(SkillEntity entity, SkillVersionEntity version) {
        List<SkillVersionEntity> versions = entity.getVersions();
        for (int i = 0; i < versions.size(); i++) {
            if (Objects.equals(versions.get(i).getVersion(), version.getVersion())) {
                versions.set(i, version);
                return;
            }
        }
        versions.add(version);
    }

    private void touchDraft(SkillEntity entity, SkillVersionEntity version) {
        entity.setCurrentDraftVersion(version.getVersion());
        entity.setDraftBaseVersion(version.getBaseVersion());
        entity.setCurrentVersionInfo(version);
        entity.setSkillStatus(SkillStatusEnum.DRAFT);
    }

    private void refreshVersionComplete(SkillVersionEntity version) {
        version.setComplete(StringUtils.hasText(version.getSkillMdObjectKey()));
        version.setUpdatedAt(LocalDateTime.now());
    }

    private SkillAssetEntity findOrCreateAsset(SkillVersionEntity versionInfo, String relativePath) {
        for (SkillAssetEntity item : versionInfo.getAssetsManifest()) {
            if (relativePath.equals(item.getPath())) {
                return item;
            }
        }
        SkillAssetEntity asset = new SkillAssetEntity();
        asset.setPath(relativePath);
        versionInfo.getAssetsManifest().add(asset);
        return asset;
    }

    private List<SkillAssetEntity> copyAssets(List<SkillAssetEntity> assets) {
        List<SkillAssetEntity> copied = new ArrayList<>();
        for (SkillAssetEntity asset : assets) {
            SkillAssetEntity item = new SkillAssetEntity();
            item.setPath(asset.getPath());
            item.setObjectKey(asset.getObjectKey());
            item.setKind(asset.getKind());
            item.setSizeBytes(asset.getSizeBytes());
            copied.add(item);
        }
        return copied;
    }

    private SkillVersionEntity copyVersion(SkillVersionEntity version) {
        SkillVersionEntity copied = new SkillVersionEntity();
        copied.setVersion(version.getVersion());
        copied.setBaseVersion(version.getBaseVersion());
        copied.setSkillMdObjectKey(version.getSkillMdObjectKey());
        copied.setPublished(version.getPublished());
        copied.setEnabled(version.getEnabled());
        copied.setAuditStatus(version.getAuditStatus());
        copied.setComplete(version.getComplete());
        copied.setCreatedAt(version.getCreatedAt());
        copied.setUpdatedAt(version.getUpdatedAt());
        copied.setAssetsManifest(copyAssets(version.getAssetsManifest()));
        return copied;
    }

    private SkillVersionBase findVersionBase(SkillEntity entity, String version) {
        SkillVersionEntity versionEntity = findVersion(entity, version);
        return versionEntity == null ? null : toVersionBase(versionEntity);
    }

    private SkillVersionBase toVersionBase(SkillVersionEntity version) {
        SkillVersionBase versionBase = new SkillVersionBase();
        versionBase.setVersion(version.getVersion());
        versionBase.setBaseVersion(version.getBaseVersion());
        versionBase.setSkillMdObjectKey(version.getSkillMdObjectKey());
        versionBase.setPublished(version.getPublished());
        versionBase.setEnabled(version.getEnabled());
        versionBase.setAuditStatus(version.getAuditStatus());
        versionBase.setComplete(version.getComplete());
        versionBase.setCreatedAt(version.getCreatedAt());
        versionBase.setUpdatedAt(version.getUpdatedAt());
        for (SkillAssetEntity asset : version.getAssetsManifest()) {
            versionBase.getAssetsManifest().add(BeanUtil.copyProperties(asset, SkillAssetMetaBase.class));
        }
        return versionBase;
    }

    private void validateVersion(String version) {
        if (version == null || version.isBlank() || version.contains("/") || version.contains("\\")
                || ".".equals(version) || "..".equals(version)) {
            throw new ServiceException(SkillError.SKILL_VERSION_INVALID);
        }
    }

    private void validateRelativePath(String relativePath) {
        if (relativePath == null || relativePath.isBlank() || relativePath.startsWith("/")
                || relativePath.contains("\\") || List.of(relativePath.split("/")).contains("..")) {
            throw new ServiceException(SkillError.SKILL_RELATIVE_PATH_INVALID);
        }
    }

    private String buildObjectKey(String skillId, String version, String relativePath) {
        return "skills/" + skillId + "/" + version + "/" + relativePath;
    }

    private String extractExtension(String relativePath) {
        int dotIndex = relativePath.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == relativePath.length() - 1) {
            return "txt";
        }
        return relativePath.substring(dotIndex + 1);
    }
}
