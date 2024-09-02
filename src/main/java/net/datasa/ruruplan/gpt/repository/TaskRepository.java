package net.datasa.ruruplan.gpt.repository;

import net.datasa.ruruplan.gpt.domain.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 활동별 레포지토리
 */
@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {
}
