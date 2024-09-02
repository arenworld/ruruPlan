package net.datasa.ruruplan.gpt.repository;

import net.datasa.ruruplan.gpt.domain.entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 여행 계획 레포지토리
 */
@Repository
public interface PlanRepository extends JpaRepository<PlanEntity, Integer> {
}
