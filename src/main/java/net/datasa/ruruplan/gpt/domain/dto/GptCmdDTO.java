package net.datasa.ruruplan.gpt.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GptCmdDTO {

    private Integer cmdNum;

    // 멤버 아이디만 받도록 했습니다. 나중에 더 많은 정보가 필요하면 수정 부탁드립니다.
    private String memberId;
    private LocalDate firstDate;
    private LocalDate lastDate;
    private Integer nights;
    private Integer days;
    private LocalTime arrival;
    private LocalTime depart;
    private String tripType;
    private Integer adult;
    private Integer children;
    private String theme1;
    private Integer theme1Weight;
    private String theme2;
    private Integer theme2Weight;
    private String theme3;
    private Integer theme3Weight;
    private Boolean density;
    private Boolean moveStatus;
    private Integer roomCost;
    private Boolean hotel;
    private Integer hotelNights;
    private Boolean motel;
    private Integer motelNights;
    private Boolean guesthouse;
    private Integer guesthouseNights;
    private Boolean hanok;
    private Integer hanokNights;

}