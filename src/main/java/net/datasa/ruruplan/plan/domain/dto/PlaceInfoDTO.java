package net.datasa.ruruplan.plan.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceInfoDTO {
    // 관광지 고유번호
    private String placeId;
    
    // 관광지명
    private String titleKor;
    
    private String titleJpa;
    
    // 관광지 주소
    private String addressKor;
    
    private String addressJpa;
    
    // x좌표, 테이블에서 varchar타입으로 만들었나보다 --> service에서 형변환 시켜줌
    private String mapX;
    
    // y좌표
    private String mapY;
    
    // 시 군 구
    private String siGunGu;
    
    // 일정대분류 (관광 식당 카페 등)
    private String contentsType;
    
    // 테마1(힐링 등등)
    private String theme1;
    
    // 테마2
    private String theme2;
    
    // 테마3
    private String theme3;
    
    // 애완동물 보유여부
    private Boolean petFriendly;
    
    // 도로편의성 - 무장애 여부
    private Boolean barrierFree;

    // 개요
    private String overviewKr;

    private String overviewJp;

    // 세계유산여부
    private Boolean heritage;

    // 안내처
    private String infocenter;
    
    // 이용시간
    private String usetimeKr;

    private String usetimeJp;

    // 휴무일
    private String restdateKr;

    private String restdateJp;

    // 요금
    private Integer fee;

    // 요금정보
    private String feeInfoKr;

    private String feeInfoJp;

    // 판매품목
    private String saleItemKr;

    private String saleItemJp;
    
    // 원본 이미지
    private String originImgUrl;

    // 섬네일 이미지
    private String smallImgUrl;
}
