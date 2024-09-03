package net.datasa.ruruplan.plan.repository;

import net.datasa.ruruplan.plan.domain.entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 여행 계획 레포지토리
 */
@Repository
public interface PlanRepository extends JpaRepository<PlanEntity, Integer> {
}
