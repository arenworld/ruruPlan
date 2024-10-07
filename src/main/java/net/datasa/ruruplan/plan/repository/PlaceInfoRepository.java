package net.datasa.ruruplan.plan.repository;

import net.datasa.ruruplan.plan.domain.entity.PlaceInfoEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PlaceInfoRepository {
    List<PlaceInfoEntity> findByTheme(String theme);
    List<String> findPlaceIdsByAddressAndThemes(String address, List<String> themes);
    List<String> findAlternativePlaceIds(String address);
    List<String> findRestaurantOrCafePlaceIds(String address, String placeType);
    Optional<PlaceInfoEntity> findById(String placeId);
    List<String> findByThemeAndExcludeExistingPlaces(String theme, List<String> existingPlaceIds);
    List<String> findByThemePlaces(String theme);
    List<String> findTourismOrCulturalPlaceIds();  // '관광' 또는 '문화시설' 테마 장소 조회
}
