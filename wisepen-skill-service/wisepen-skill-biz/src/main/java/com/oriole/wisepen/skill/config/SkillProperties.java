package com.oriole.wisepen.skill.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wisepen.skill")
public class SkillProperties {
    private String storageRoot = "/data/wisepen/skills";

    private String tempRoot = "/tmp/wisepen/skills";

    private Boolean auditEnabled = true;

    private Boolean hotLoadEnabled = true;

    private Boolean lazyAssetFetchEnabled = true;
}
