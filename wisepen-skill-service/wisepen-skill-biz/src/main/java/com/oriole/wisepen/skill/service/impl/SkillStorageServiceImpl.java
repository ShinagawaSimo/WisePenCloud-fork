package com.oriole.wisepen.skill.service.impl;

import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.common.core.exception.ServiceException;
import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitReqDTO;
import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitRespDTO;
import com.oriole.wisepen.file.storage.api.enums.StorageSceneEnum;
import com.oriole.wisepen.file.storage.api.feign.RemoteStorageService;
import com.oriole.wisepen.skill.service.ISkillStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillStorageServiceImpl implements ISkillStorageService {

    private final RemoteStorageService remoteStorageService;

    @Override
    public UploadInitRespDTO initManifestUpload(String skillId, String version, String md5, Long expectedSize) {
        return initUpload(buildObjectKey(skillId, version, "SKILL.md"), "md", md5, expectedSize);
    }

    @Override
    public UploadInitRespDTO initAssetUpload(String skillId, String version, String relativePath, String md5, Long expectedSize) {
        return initUpload(buildObjectKey(skillId, version, relativePath), extractExtension(relativePath), md5, expectedSize);
    }

    @Override
    public String getDownloadUrl(String objectKey, Long durationSeconds) {
        return unwrap(remoteStorageService.getDownloadUrl(objectKey, durationSeconds), "获取 Skill 文件下载地址失败");
    }

    @Override
    public void deleteFiles(List<String> objectKeys) {
        unwrapVoid(remoteStorageService.deleteFiles(objectKeys), "删除 Skill 文件失败");
    }

    private UploadInitRespDTO initUpload(String objectKey, String extension, String md5, Long expectedSize) {
        return unwrap(remoteStorageService.initUpload(UploadInitReqDTO.builder()
                .md5(md5)
                .extension(extension)
                .scene(StorageSceneEnum.PRIVATE_DOC)
                .objectKey(objectKey)
                .expectedSize(expectedSize)
                .build()), "初始化 Skill 文件上传失败");
    }

    private String buildObjectKey(String skillId, String version, String relativePath) {
        return "skills/" + skillId + "/" + version + "/" + relativePath;
    }

    private String extractExtension(String relativePath) {
        int dotIndex = relativePath.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == relativePath.length() - 1) {
            return "bin";
        }
        return relativePath.substring(dotIndex + 1);
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
