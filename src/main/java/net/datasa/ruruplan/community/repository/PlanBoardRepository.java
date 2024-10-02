package net.datasa.ruruplan.community.repository;

import net.datasa.ruruplan.community.domain.entity.PlanBoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanBoardRepository extends JpaRepository<PlanBoardEntity, Integer> {

    List<PlanBoardEntity> findAllByOrderByBoardNumDesc();

}