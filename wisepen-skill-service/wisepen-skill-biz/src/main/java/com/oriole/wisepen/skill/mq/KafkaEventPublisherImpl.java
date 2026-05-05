package com.oriole.wisepen.skill.mq;

import com.oriole.wisepen.common.mq.ReliablePublisher;
import com.oriole.wisepen.skill.domain.mq.SkillAuditMessage;
import com.oriole.wisepen.skill.domain.mq.SkillLoadMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.oriole.wisepen.skill.constant.MqTopicConstants.TOPIC_SKILL_AUDIT;
import static com.oriole.wisepen.skill.constant.MqTopicConstants.TOPIC_SKILL_LOAD;

@Slf4j
@Component
public class KafkaEventPublisherImpl implements IEventPublisher {

    @Resource
    private ReliablePublisher reliablePublisher;

    @Override
    public void publishSkillAuditEvent(String skillId, String ownerId, String manifestPath) {
        SkillAuditMessage message = SkillAuditMessage.builder()
                .skillId(skillId)
                .ownerId(ownerId)
                .manifestPath(manifestPath)
                .build();
        log.debug("skillAudit published topic={} skillId={}", TOPIC_SKILL_AUDIT, skillId);
        reliablePublisher.publish(TOPIC_SKILL_AUDIT, skillId, message, skillId);
    }

    @Override
    public void publishSkillLoadEvent(String skillId, String ownerId, String manifestPath) {
        SkillLoadMessage message = SkillLoadMessage.builder()
                .skillId(skillId)
                .ownerId(ownerId)
                .manifestPath(manifestPath)
                .build();
        log.debug("skillLoad published topic={} skillId={}", TOPIC_SKILL_LOAD, skillId);
        reliablePublisher.publish(TOPIC_SKILL_LOAD, skillId, message, skillId);
    }
}
