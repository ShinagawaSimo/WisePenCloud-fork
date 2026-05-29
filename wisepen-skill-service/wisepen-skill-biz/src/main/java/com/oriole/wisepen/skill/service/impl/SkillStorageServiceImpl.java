package com.oriole.wisepen.skill.service.impl;

import com.oriole.wisepen.common.core.domain.IResult;
import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.common.core.exception.ServiceException;
import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitReqDTO;
import com.oriole.wisepen.file.storage.api.domain.dto.UploadInitRespDTO;
import com.oriole.wisepen.file.storage.api.enums.StorageSceneEnum;
import com.oriole.wisepen.file.storage.api.feign.RemoteStorageService;
import com.oriole.wisepen.skill.domain.dto.SkillAssetUpdateItemDTO;
import com.oriole.wisepen.skill.domain.dto.SkillAssetUpdateRespItemDTO;
import com.oriole.wisepen.skill.exception.SkillError;
import com.oriole.wisepen.skill.service.ISkillStorageService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
    public List<SkillAssetUpdateRespItemDTO> batchInitAssetUpload(String skillId, String version, List<SkillAssetUpdateItemDTO> assets) {
        List<SkillAssetUpdateRespItemDTO> responses = new ArrayList<>();
        for (SkillAssetUpdateItemDTO item : assets) {
            UploadInitRespDTO upload = initAssetUpload(skillId, version, item.getRelativePath(), item.getMd5(), item.getExpectedSize());
            responses.add(SkillAssetUpdateRespItemDTO.builder()
                    .relativePath(item.getRelativePath())
                    .updateMode(item.getUpdateMode())
                    .objectKey(upload.getObjectKey())
                    .putUrl(upload.getPutUrl())
                    .callbackHeader(upload.getCallbackHeader())
                    .kind(item.getKind())
                    .flashUploaded(upload.getFlashUploaded())
                    .build());
        }
        return responses;
    }

    @Override
    public String readBaseText(String objectKey) {
        String downloadUrl = unwrap(remoteStorageService.getDownloadUrl(objectKey, null), SkillError.SKILL_UPLOAD_INIT_FAILED);
        try (InputStream inputStream = URI.create(downloadUrl).toURL().openStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new ServiceException(SkillError.SKILL_PATCH_INVALID, ex.getMessage());
        }
    }

    @Override
    public UploadInitRespDTO applyTextPatchAndWriteObject(String targetObjectKey, String extension, String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        UploadInitRespDTO upload = initUpload(targetObjectKey, extension, DigestUtils.md5Hex(bytes), (long) bytes.length);
        if (Boolean.TRUE.equals(upload.getFlashUploaded()) || upload.getPutUrl() == null) {
            return upload;
        }
        writeBytes(upload.getPutUrl(), upload.getCallbackHeader(), bytes);
        return upload;
    }

    private void writeBytes(String putUrl, String callbackHeader, byte[] bytes) {
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(putUrl).toURL().openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setFixedLengthStreamingMode(bytes.length);
            if (callbackHeader != null && !callbackHeader.isBlank()) {
                connection.setRequestProperty("x-oss-callback", callbackHeader);
            }
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(bytes);
            }
            connection.getResponseCode();
        } catch (Exception ex) {
            throw new ServiceException(SkillError.SKILL_UPLOAD_INIT_FAILED, ex.getMessage());
        }
    }

    private UploadInitRespDTO initUpload(String objectKey, String extension, String md5, Long expectedSize) {
        return unwrap(remoteStorageService.initUpload(UploadInitReqDTO.builder()
                .md5(md5)
                .extension(extension)
                .scene(StorageSceneEnum.PRIVATE_DOC)
                .objectKey(objectKey)
                .expectedSize(expectedSize)
                .build()), SkillError.SKILL_UPLOAD_INIT_FAILED);
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

    private <T> T unwrap(R<T> response, IResult message) {
        if (response == null) {
            throw new ServiceException(message);
        }
        if (response.getCode() == null || response.getCode() != 200) {
            throw new ServiceException(message, response.getMsg());
        }
        if (response.getData() == null) {
            throw new ServiceException(message);
        }
        return response.getData();
    }
}
