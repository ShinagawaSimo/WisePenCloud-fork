package com.oriole.wisepen.skill.mq;

public interface IEventPublisher {
    void publishSkillAuditEvent(String skillId, String ownerId, String manifestPath);

    void publishSkillLoadEvent(String skillId, String ownerId, String manifestPath);
}
