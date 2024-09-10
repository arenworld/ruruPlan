package net.datasa.ruruplan.plan.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.datasa.ruruplan.plan.domain.entity.PlaceInfoEntity;
import net.datasa.ruruplan.plan.domain.entity.QPlaceInfoEntity;
import net.datasa.ruruplan.plan.domain.entity.QTaskEntity;
import net.datasa.ruruplan.plan.repository.PlaceInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class PlaceInfoRepositoryImpl implements PlaceInfoRepository {

    @PersistenceContext
    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    private QPlaceInfoEntity placeInfoEntity = QPlaceInfoEntity.placeInfoEntity;

    @Autowired
    public PlaceInfoRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<PlaceInfoEntity> findByTheme(String theme) {

      return queryFactory.selectFrom(placeInfoEntity)
                .where(
                        placeInfoEntity.theme1.eq(theme)
                        .or(placeInfoEntity.theme2.eq(theme))
                        .or(placeInfoEntity.theme3.eq(theme))
                )
                .fetch();
    }
}
