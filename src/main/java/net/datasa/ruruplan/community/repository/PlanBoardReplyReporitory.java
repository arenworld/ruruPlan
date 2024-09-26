package net.datasa.ruruplan.community.repository;

import net.datasa.ruruplan.community.domain.entity.PlanBoardReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanBoardReplyReporitory extends JpaRepository<PlanBoardReplyEntity, Integer> {
}
