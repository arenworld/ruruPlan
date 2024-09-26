package net.datasa.ruruplan.community.repository;

import net.datasa.ruruplan.community.domain.entity.SavePlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavePlanRepository extends JpaRepository<SavePlanEntity, Integer> {
}
