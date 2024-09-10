package net.datasa.ruruplan.plan.repository.jpa;

import net.datasa.ruruplan.plan.domain.entity.TaskEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 활동별 레포지토리
 */
@Repository
public interface TaskJpaRepository extends JpaRepository<TaskEntity, Integer> {

  List<TaskEntity> findByPlanPlanNum(int planNum, Sort sort);
}
