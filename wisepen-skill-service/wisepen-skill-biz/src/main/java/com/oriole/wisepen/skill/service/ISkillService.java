package com.oriole.wisepen.skill.service;

import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitRespDTO;
import com.oriole.wisepen.skill.domain.dto.PublishedSkillSnapshotRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillAssetUploadInitReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillCreateReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillCurrentDraftRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillDraftVersionCreateReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillFilesFullUpdateReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillFilesFullUpdateRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillInfoRespDTO;
import com.oriole.wisepen.skill.domain.dto.SkillManifestUploadInitReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillVersionListReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillUpdateReqDTO;
import com.oriole.wisepen.skill.domain.dto.SkillVersionBaseListRespDTO;

import java.util.List;

public interface ISkillService {
    String createSkill(SkillCreateReqDTO dto, String userId);

    void deleteSkills(List<String> skillIds);

    void updateSkill(SkillUpdateReqDTO dto);

    SkillInfoRespDTO getSkillInfo(String skillId);

    UploadInitRespDTO initManifestUpload(SkillManifestUploadInitReqDTO dto);

    UploadInitRespDTO initAssetUpload(SkillAssetUploadInitReqDTO dto);

    String createDraftVersion(SkillDraftVersionCreateReqDTO dto);

    List<SkillVersionBaseListRespDTO> listSkillVersions(SkillVersionListReqDTO dto);

    SkillCurrentDraftRespDTO getCurrentDraftVersion(SkillVersionListReqDTO dto);

    void publishSkillVersion(String skillId, String version);

    void offlineSkill(String skillId);

    PublishedSkillSnapshotRespDTO getPublishedSkillSnapshot(String skillId);

    SkillFilesFullUpdateRespDTO fullUpdateSkillFiles(SkillFilesFullUpdateReqDTO dto);
}
