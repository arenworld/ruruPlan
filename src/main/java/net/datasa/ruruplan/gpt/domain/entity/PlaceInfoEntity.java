package net.datasa.ruruplan.gpt.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="ruru_place_info")
public class PlaceInfoEntity {


    @Id
    @Column(name = "place_id", length = 50)
    private String placeId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Column(name = "map_x", length = 20)
    private String mapX;

    @Column(name = "map_y", length = 20)
    private String mapY;

    @Column(name = "si_gun_gu", length = 20)
    private String siGunGu;

    @Column(name = "contents_type", length = 20)
    private String contentsType;

    @Column(name = "theme1", length = 20)
    private String theme1;

    @Column(name = "theme2", length = 20)
    private String theme2;

    @Column(name = "theme3", length = 20)
    private String theme3;

    @Column(name = "pet_friendly", nullable = false)
    private Boolean petFriendly;

    @Column(name = "barrier_free", nullable = false)
    private Boolean barrierFree;
}