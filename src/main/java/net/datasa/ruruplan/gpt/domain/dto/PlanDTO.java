package net.datasa.ruruplan.gpt.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDTO {

    // plan 고유번호(자동증가)
    private Integer planNum;
    // 질문번호 참조
    private Integer cmdNum;
    // 멤버 아이디 참조
    private String memberId;
    // 여행 시작일
    private LocalDate startDate;
    // 여행 끝나는 날
    private LocalDate endDate;
    // 계획 만들어진 시간
    private LocalDateTime planCreateDate;
    // 계획 수정 시간
    private LocalDateTime planUpdateDate;
    // 마이페이지에 보여질 플랜 커버 사진?
    private String coverImageUrl;

}
