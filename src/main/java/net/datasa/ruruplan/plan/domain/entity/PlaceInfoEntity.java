package net.datasa.ruruplan.plan.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 관광지 정보 Entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="ruru_place_info")
public class PlaceInfoEntity {


    // 관광지 고유번호
    @Id
    @Column(name = "place_id", length = 50)
    private String placeId;

    // 광광지명
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    // 관광지 주소
    @Column(name = "address", nullable = false, length = 200)
    private String address;

    // x좌표
    @Column(name = "map_x", length = 20)
    private String mapX;

    // y좌표
    @Column(name = "map_y", length = 20)
    private String mapY;

    // 시 군 구
    @Column(name = "si_gun_gu", length = 20)
    private String siGunGu;

    // 여행 타입
    @Column(name = "contents_type", length = 20)
    private String contentsType;

    // 테마1(힐링 등등)
    @Column(name = "theme1", length = 20)
    private String theme1;

    // 테마2
    @Column(name = "theme2", length = 20)
    private String theme2;

    // 테마3
    @Column(name = "theme3", length = 20)
    private String theme3;

    // 애완동물 보유여부
    @Column(name = "pet_friendly", nullable = false)
    private Boolean petFriendly;

    // 장애 여부
    @Column(name = "barrier_free", nullable = false)
    private Boolean barrierFree;

}