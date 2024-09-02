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

    private Integer planNum;
    private Integer cmdNum;
    private String memberId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime planCreateDate;
    private LocalDateTime planUpdateDate;
    private String coverImageUrl;

}
