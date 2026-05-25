package com.oriole.wisepen.skill.service.impl;

import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.common.core.exception.ServiceException;
import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitReqDTO;
import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitRespDTO;
import com.oriole.wisepen.file.storage.api.enums.StorageSceneEnum;
import com.oriole.wisepen.file.storage.api.feign.RemoteStorageService;
import com.oriole.wisepen.skill.config.SkillProperties;
import com.oriole.wisepen.skill.service.ISkillStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillStorageServiceImpl implements ISkillStorageService {

    private final RemoteStorageService remoteStorageService;
    private final SkillProperties skillProperties;

    @Override
    public UploadInitRespDTO initManifestUpload(String ownerId, String skillId, String md5, Long expectedSize) {
        return initUpload(ownerId, skillId, "md", md5, expectedSize);
    }

    @Override
    public UploadInitRespDTO initAssetUpload(String ownerId, String skillId, String extension, String md5, Long expectedSize) {
        return initUpload(ownerId, skillId, extension, md5, expectedSize);
    }

    @Override
    public String getDownloadUrl(String objectKey, Long durationSeconds) {
        return unwrap(remoteStorageService.getDownloadUrl(objectKey, durationSeconds), "获取 Skill 文件下载地址失败");
    }

    @Override
    public void deleteFiles(List<String> objectKeys) {
        unwrapVoid(remoteStorageService.deleteFiles(objectKeys), "删除 Skill 文件失败");
    }

    private UploadInitRespDTO initUpload(String ownerId, String skillId, String extension, String md5, Long expectedSize) {
        return unwrap(remoteStorageService.initUpload(UploadInitReqDTO.builder()
                .md5(md5)
                .extension(extension)
                .scene(StorageSceneEnum.PRIVATE_DOC)
                .bizTag(buildStorageBizTag(ownerId, skillId))
                .expectedSize(expectedSize)
                .build()), "初始化 Skill 文件上传失败");
    }

    private String buildStorageBizTag(String ownerId, String skillId) {
        return skillProperties.getStorageBizTagPrefix() + "/" + ownerId + "/" + skillId;
    }

    private <T> T unwrap(R<T> response, String message) {
        if (response == null || response.getData() == null) {
            throw new ServiceException(message);
        }
        return response.getData();
    }

    private void unwrapVoid(R<Void> response, String message) {
        if (response == null) {
            throw new ServiceException(message);
        }
    }
}
