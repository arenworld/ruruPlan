package net.datasa.ruruplan.community.repository;

import net.datasa.ruruplan.community.domain.entity.PlanBoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanBoardRepository extends JpaRepository<PlanBoardEntity, Integer> {

    List<PlanBoardEntity> findAllByOrderByBoardNumDesc();
    Page<PlanBoardEntity> findAllByOrderByBoardNumDesc(int num, Pageable pageable);

    //태그1,2,3,4,5 중 하나에 문자열이 포함된 게시글 조회
    List<PlanBoardEntity> findByTag1ContainingOrTag2ContainingOrTag3ContainingOrTag4ContainingOrTag5Containing(String serchType1,
                                                                                                               String serchType2,
                                                                                                               String serchType3,
                                                                                                               String serchType4,
                                                                                                               String serchType5,
                                                                                                                Sort sort);

    //제목 검색 1페이지 분량
    Page<PlanBoardEntity> findByTag1ContainingOrTag2ContainingOrTag3ContainingOrTag4ContainingOrTag5Containing(
            String keyword1, String keyword2, String keyword3,String keyword4,String keyword5, Pageable pageable
    );


}