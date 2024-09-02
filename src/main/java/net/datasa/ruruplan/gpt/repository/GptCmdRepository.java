package net.datasa.ruruplan.gpt.repository;

import net.datasa.ruruplan.gpt.domain.entity.GptCmdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * GPT 질문 레포지토리
 */
@Repository
public interface GptCmdRepository extends JpaRepository<GptCmdEntity, Integer> {

}

