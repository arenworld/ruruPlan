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

    @Override
    public List<String> findPlaceIdsByAddressAndThemes(String address, List<String> themes) {
        return queryFactory.select(placeInfoEntity.placeId)
                .from(placeInfoEntity)
                .where(
                        placeInfoEntity.siGunGu.eq(address)
                                .and(
                                        placeInfoEntity.theme1.in(themes)
                                                .or(placeInfoEntity.theme2.in(themes))
                                                .or(placeInfoEntity.theme3.in(themes))
                                )
                )
                .fetch();
    }

    @Override
    public List<String> findAlternativePlaceIds(String address) {
        return queryFactory.select(placeInfoEntity.placeId)
                .from(placeInfoEntity)
                .where(
                        placeInfoEntity.siGunGu.eq(address)
                                .and(
                                        placeInfoEntity.theme1.notIn("식당", "카페")
                                                .and(placeInfoEntity.theme2.notIn("식당", "카페"))
                                                .and(placeInfoEntity.theme3.notIn("식당", "카페"))
                                )
                )
                .fetch();
    }

    @Override
    public List<String> findRestaurantOrCafePlaceIds(String address, String placeType) {
        return queryFactory.select(placeInfoEntity.placeId)
                .from(placeInfoEntity)
                .where(
                        placeInfoEntity.siGunGu.eq(address)
                                .and(
                                        placeInfoEntity.theme1.eq(placeType)
                                                .or(placeInfoEntity.theme2.eq(placeType))
                                                .or(placeInfoEntity.theme3.eq(placeType))
                                )
                )
                .fetch();
    }
}
