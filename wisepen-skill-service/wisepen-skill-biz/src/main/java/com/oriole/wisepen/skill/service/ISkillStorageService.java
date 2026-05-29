package com.oriole.wisepen.skill.service;

import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillAssetUpdateItemDTO;
import com.oriole.wisepen.skill.domain.dto.SkillAssetUpdateRespItemDTO;

import java.util.List;

public interface ISkillStorageService {
    UploadInitRespDTO initManifestUpload(String skillId, String version, String md5, Long expectedSize);

    UploadInitRespDTO initAssetUpload(String skillId, String version, String relativePath, String md5, Long expectedSize);

    List<SkillAssetUpdateRespItemDTO> batchInitAssetUpload(String skillId, String version, List<SkillAssetUpdateItemDTO> assets);

    String readBaseText(String objectKey);

    UploadInitRespDTO applyTextPatchAndWriteObject(String targetObjectKey, String extension, String content);
}
