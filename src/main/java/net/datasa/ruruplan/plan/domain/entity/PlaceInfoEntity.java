package net.datasa.ruruplan.plan.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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

    // 관광지명(한글)
    @Column(name = "title_kr", nullable = false, length = 100)
    private String titleKor;

    // 관광지명(일본어)
    @Column(name = "title_jp", nullable = false, length = 200)
    private String titleJap;


    // 관광지 주소(한글)
    @Column(name = "address_kr", nullable = false, length = 200)
    private String addressKor;

    // 관광지 주소(일본어)
    @Column(name = "address_jp", nullable = false, length = 200)
    private String addressJap;

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

    // 펫프렌들리
    @Column(name = "pet_friendly", nullable = false)
    private Boolean petFriendly;

    // 도로편의성 - 무장애 여부
    @Column(name = "barrier_free", nullable = false)
    private Boolean barrierFree;

    // 개요(한국어)
    @Column(name = "overview_kr", columnDefinition = "text")
    private String overviewKr;

    // 개요(일본어)
    @Column(name = "overview_jp", columnDefinition = "text")
    private String overviewJp;

    //세계유산여부
    @Column(name = "heritage", columnDefinition = "tinyint(1) check(enabled in (0, 1))")
    @ColumnDefault("0")
    private Boolean heritage;

    // 안내센터, 연락망
    @Column(name = "infocenter", length=200)
    private String infocenter;

    // 이용시간(한국어)
    @Column(name = "usetime_kr", length=2000)
    private String usetimeKr;

    // 이용시간(일본어)
    @Column(name = "usetime_jp", length=2000)
    private String usetimeJp;

    // 휴무일(한국어)
    @Column(name = "restdate_kr", length=200)
    private String restdateKr;

    // 휴무일(일본어)
    @Column(name = "restdate_jp", length=200)
    private String restdateJp;

    // 요금
    @Column(name = "fee", length=200)
    private Integer fee;

    // 요금정보(한국어)
    @Column(name = "fee_info_kr", length=200)
    private String feeInfoKr;

    // 요금정보(일본어)
    @Column(name = "fee_info_jp", length=200)
    private String feeInfo_Jp;

    // 판매품목(한국어)
    @Column(name = "sale_item_kr", length=200)
    private String saleItemKr;

    // 판매품목(일본어)
    @Column(name = "sale_item_jp", length=200)
    private String saleItemJp;

    // 원본 이미지
    @Column(name = "origin_imgurl", length=200)
    private String originImgUrl;

    // 섬네일 이미지
    @Column(name = "small_imgurl", length=200)
    private String smallImgUrl;





}