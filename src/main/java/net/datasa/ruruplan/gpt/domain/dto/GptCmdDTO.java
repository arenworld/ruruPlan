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

    // 질문 고유번호(자동증가)
    private Integer cmdNum;
    // 멤버 아이디
    private String memberId;
    // 여행 첫날
    private LocalDate firstDate;
    // 여행 마지막날
    private LocalDate lastDate;
    // 몇 박
    private Integer nights;
    // 며칠
    private Integer days;
    // 도착시간
    private LocalTime arrival;
    // 떠나는 시간
    private LocalTime depart;
    // 여행타입(혼자, 가족 등등)
    private String tripType;
    // 어른 수
    private Integer adult;
    // 아이 수
    private Integer children;
    // 테마1
    private String theme1;
    // 테마1 가중치
    private Integer theme1Weight;
    // 테마2
    private String theme2;
    // 테마2 가중치
    private Integer theme2Weight;
    // 테마3
    private String theme3;
    // 테마3 가중치
    private Integer theme3Weight;
    // 밀도(빽뺵, 여유?)
    private Boolean density;
    // 숙박 이동여부
    private Boolean moveStatus;
    // 숙소 비용(저렴, 적당, 호화, 반반)
    private Integer roomCost;
    // 호텔 숙박여부
    private Boolean hotel;
    // 호텔 숙박일
    private Integer hotelNights;
    // 모텔 숙박여부
    private Boolean motel;
    // 모텔 숙박일
    private Integer motelNights;
    // 게하 숙박여부
    private Boolean guesthouse;
    // 게하 숙박일
    private Integer guesthouseNights;
    // 한옥 숙박여부
    private Boolean hanok;
    // 한옥 숙박일
    private Integer hanokNights;
}