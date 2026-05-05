package com.oriole.wisepen.skill.repository;

import com.oriole.wisepen.skill.domain.entity.SkillEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillRepository extends MongoRepository<SkillEntity, String> {
    Optional<SkillEntity> findBySkillId(String skillId);
}
