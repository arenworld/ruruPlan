package net.datasa.ruruplan.plan.repository.impl;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.datasa.ruruplan.plan.domain.dto.TaskDTO;
import net.datasa.ruruplan.plan.domain.entity.QTaskEntity;
import net.datasa.ruruplan.plan.domain.entity.TaskEntity;
import net.datasa.ruruplan.plan.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Transactional
@Repository
public class TaskRepositoryImpl implements TaskRepository {

    @PersistenceContext
    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    private QTaskEntity taskEntity = QTaskEntity.taskEntity;

    @Autowired
    public TaskRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * getPlanLocations , 지도 맵 그리는 가장 기본 ajax에서 사용되는 dayTaskList 일자별 일정리스트를
     * @param planNum
     * @param dayNum
     * @return
     */
    @Override
    public List<TaskEntity> dayTaskList(Integer planNum, Integer dayNum) {
        return  queryFactory.selectFrom(taskEntity)
                .where(taskEntity.plan.planNum.eq(planNum)
                        .and(taskEntity.dateN.eq(dayNum)))
                .fetch();
    }

    /**
     * duration 한 개 수정시, 해당 일자에 대한 모든 일장의 첫시간 수정
     * @param planNum
     * @param dayNum
     * @param fromPoint
     * @return
     */
    @Override
    public List<TaskEntity> updateDurationList(Integer planNum, int dayNum, LocalTime fromPoint) {
        return queryFactory.selectFrom(taskEntity)
                .where(taskEntity.plan.planNum.eq(planNum)
                    .and(taskEntity.dateN.eq(dayNum))
                    .and(taskEntity.startTime.gt(fromPoint) ))
                .orderBy(taskEntity.startTime.asc())
                .fetch();
    }

    @Override
    public List<TaskDTO> getDayTaskList(Integer planNum, Integer dateN) {
        // 모든 컬럼을 불러오는 예시
//        queryFactory.selectFrom(taskEntity)
//                .where(taskEntity.plan.planNum.eq(2))
//                .fetch();   // 한 건 조회시에는 fetchOne()
        return queryFactory.select(
                        Projections.constructor(TaskDTO.class,
                                taskEntity.plan.planNum,
                                taskEntity.dateN
                        )
                )
                .from(taskEntity)
                .where(taskEntity.plan.planNum.eq(planNum)
                        .and(taskEntity.dateN.eq(dateN)))
                .fetch();
    }

    @Override
    public List<TaskEntity> getDayLocations(Integer planNum, Integer dayNum) {
        return queryFactory.selectFrom(taskEntity)
                .where(taskEntity.plan.planNum.eq(planNum)
                        .and(taskEntity.dateN.eq(dayNum)))
                .fetch();
    }

    @Override
    public void save(TaskEntity taskEntity) {
        if (taskEntity.getTaskNum() == null) {
            // 새로운 TaskEntity인 경우
            em.persist(taskEntity);
        } else {
            // 기존 TaskEntity인 경우
            em.merge(taskEntity);
        }
    }


}
