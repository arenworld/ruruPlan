package net.datasa.ruruplan.plan.repository.jpa;

import net.datasa.ruruplan.plan.domain.entity.PlanEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 여행 계획 레포지토리
 */
@Repository
public interface PlanJpaRepository extends JpaRepository<PlanEntity, Integer> {
    List<PlanEntity> findAllByMember_MemberId(String memberId, Sort sort); // 아 이건 새미가 만든거구나
}
