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
import java.util.Optional;

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

    // 테마 1, 2, 3 중 하나라도 일치하는 장소를 찾고 기존 장소들을 제외하는 메서드
    @Override
    public List<String> findByThemeAndExcludeExistingPlaces(String theme, List<String> existingPlaceIds) {
        return queryFactory.select(placeInfoEntity.placeId)
                .from(placeInfoEntity)
                .where(
                        (placeInfoEntity.theme1.in(theme)
                                .or(placeInfoEntity.theme2.in(theme))
                                .or(placeInfoEntity.theme3.in(theme)))
                                .and(placeInfoEntity.placeId.notIn(existingPlaceIds))
                )
                .fetch();
    }

    @Override
    public List<String> findByThemePlaces(String theme) {
        return queryFactory.select(placeInfoEntity.placeId)
                .from(placeInfoEntity)
                .where(
                        (placeInfoEntity.theme1.in(theme)
                                .or(placeInfoEntity.theme2.in(theme))
                                .or(placeInfoEntity.theme3.in(theme)))
                )
                .fetch();
    }


    public Optional<PlaceInfoEntity> findById(String placeId) {
        if (placeId == null) {
            return Optional.empty(); // placeId가 null이면 빈 값을 반환
        }

        PlaceInfoEntity placeInfoEntity = queryFactory.selectFrom(QPlaceInfoEntity.placeInfoEntity)
                .where(QPlaceInfoEntity.placeInfoEntity.placeId.eq(placeId))
                .fetchOne();

        return Optional.ofNullable(placeInfoEntity);
    }
}
