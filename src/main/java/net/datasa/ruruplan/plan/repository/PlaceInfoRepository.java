package net.datasa.ruruplan.plan.repository;

import net.datasa.ruruplan.plan.domain.entity.PlaceInfoEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PlaceInfoRepository {
    List<PlaceInfoEntity> findByTheme(String theme);

}
