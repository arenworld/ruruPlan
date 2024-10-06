package net.datasa.ruruplan.community.repository;

import net.datasa.ruruplan.community.domain.entity.SavePlanEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavePlanRepository extends JpaRepository<SavePlanEntity, Integer> {
    List<SavePlanEntity> findAllByMember_MemberId(String memberId, Sort sort);
}
