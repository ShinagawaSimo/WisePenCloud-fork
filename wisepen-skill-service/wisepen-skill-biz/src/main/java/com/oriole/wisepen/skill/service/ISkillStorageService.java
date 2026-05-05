package com.oriole.wisepen.skill.service;

import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitRespDTO;

import java.util.List;

public interface ISkillStorageService {
    UploadInitRespDTO initManifestUpload(String ownerId, String skillId, String md5, Long expectedSize);

    UploadInitRespDTO initAssetUpload(String ownerId, String skillId, String extension, String md5, Long expectedSize);

    String getDownloadUrl(String objectKey, Long durationSeconds);

    void deleteFiles(List<String> objectKeys);
}
