package net.datasa.ruruplan.gpt.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceInfoDTO {

    private String placeId;
    private String title;
    private String address;
    private String mapX;
    private String mapY;
    private String siGunGu;
    private String contentsType;
    private String theme1;
    private String theme2;
    private String theme3;
    private Boolean petFriendly;
    private Boolean barrierFree;
}
