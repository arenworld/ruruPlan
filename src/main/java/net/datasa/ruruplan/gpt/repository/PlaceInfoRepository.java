package net.datasa.ruruplan.gpt.repository;

import net.datasa.ruruplan.gpt.domain.entity.PlaceInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 관광지 레포지토리
 */
@Repository
public interface PlaceInfoRepository extends JpaRepository<PlaceInfoEntity, String> {
}
