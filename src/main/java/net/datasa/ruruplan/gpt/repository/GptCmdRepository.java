package net.datasa.ruruplan.gpt.repository;

import net.datasa.ruruplan.gpt.domain.entity.GptCmdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GptCmdRepository extends JpaRepository<GptCmdEntity, Integer> {

}

