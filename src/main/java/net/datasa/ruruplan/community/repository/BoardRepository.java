package net.datasa.ruruplan.community.repository;


import net.datasa.ruruplan.community.domain.entity.BoardEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 게시판 관련 repository
 */

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {
    List<BoardEntity> findAllByOrderByBoardNumDesc();
}
