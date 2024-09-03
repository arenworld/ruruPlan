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
    // 광광지명
    private String title;
    // 관광지 주소
    private String address;
    // x좌표
    private String mapX;
    // y좌표
    private String mapY;
    // 시 군 구
    private String siGunGu;
    // 여행 타입
    private String contentsType;
    // 테마1(힐링 등등)
    private String theme1;
    // 테마2
    private String theme2;
    // 테마3
    private String theme3;
    // 애완동물 보유여부
    private Boolean petFriendly;
    // 장애 여부
    private Boolean barrierFree;
}
