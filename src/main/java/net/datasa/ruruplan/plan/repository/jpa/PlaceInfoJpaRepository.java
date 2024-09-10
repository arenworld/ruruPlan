package net.datasa.ruruplan.plan.repository.jpa;

import net.datasa.ruruplan.plan.domain.entity.PlaceInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * jpa를 상속받은 일반 레포지토리
 * 장소정보
 */
@Repository
public interface PlaceInfoJpaRepository extends JpaRepository<PlaceInfoEntity, String> {
    //jpa기술을 쓰기 때문에 extends를 상속받지만
    //dsl을 쓰면 extends가 없다
}
