package net.datasa.ruruplan.community.repository;



import net.datasa.ruruplan.community.domain.entity.ReplyEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 리플 관련 repository
 */

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Integer> {

    //한 게시글의 리플
    List<ReplyEntity> findByBoard_BoardNum(int boardNum, Sort sort);

}
