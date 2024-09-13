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

    // 테마2
    private String theme2;

    // 테마3
    private String theme3;

    // 밀도(빽뺵, 여유?)
    private Boolean density;

}