package net.datasa.ruruplan.plan.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDTO {

    // plan 고유번호(자동증가)
    private Integer planNum;

    // plan 이름
    private String planName;

    // 질문번호 참조
    private Integer cmdNum;

    // 멤버 아이디 참조
    private String memberId;

    // 여행 시작일
    private LocalDate startDate;

    // 여행 끝나는 날
    private LocalDate endDate;

    // 사용자가 선택한 테마1
    private String theme1;

    // 사용자가 선택한 테마2
    private String theme2;

    // 사용자가 선택한 테마3
    private String theme3;

    // 계획 만들어진 시간
    private LocalDateTime planCreateDate;

    // 계획 수정 시간
    private LocalDateTime planUpdateDate;

    // 마이페이지에 보여질 플랜 커버 사진?
    private String coverImageUrl;

    //DB에는 없으나, entity에 정의한 taskList
    List<TaskDTO> taskList;
}
