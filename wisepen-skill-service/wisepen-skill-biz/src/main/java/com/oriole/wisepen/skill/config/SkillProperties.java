package com.oriole.wisepen.skill.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wisepen.skill")
public class SkillProperties {
    /**
     * Skill 文件在对象存储中的业务隔离前缀。
     * Skill 服务只维护逻辑命名空间，不再直接拼本地 storageRoot 路径。
     */
    private String storageBizTagPrefix = "skill";

    private Boolean auditEnabled = true;

    private Boolean hotLoadEnabled = true;

    private Boolean lazyAssetFetchEnabled = true;
}
