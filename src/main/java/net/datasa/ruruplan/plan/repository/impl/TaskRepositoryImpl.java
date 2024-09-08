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
    public List<TaskEntity> getDayLocations(Integer planNum, Integer dateNum) {
        return queryFactory.selectFrom(taskEntity)
                .where(taskEntity.plan.planNum.eq(planNum)
                        .and(taskEntity.dateN.eq(dateNum)))
                .fetch();
    }
}
