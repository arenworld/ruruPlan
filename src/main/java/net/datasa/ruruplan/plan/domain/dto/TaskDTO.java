package net.datasa.ruruplan.plan.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {

    // 활동 별 번호(자동증가)
    private Integer taskNum;

    // 플랜번호 참조(같은 여행일정 내에서는 같은 번호)
    private Integer planNum;

    // 장소 고유번호 참조
    private PlaceInfoDTO place;

    // 멤버 아이디 참조
    private String memberId;

    // 여행 중 며칠째?
    private Integer dateN;

    // 활동 시작 시간
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    // 활동 시간 (Duration)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime duration;

    // 활동 끝나는 시간
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    // 활동명(택시, 도보, 대중교통, 식사, 관광, 숙소)
    private String task;

    // 활동 비용
    private Integer cost;

    // 사용자 메모
    private String memo;

    public TaskDTO(Integer planNum, Integer DateN) {
        this.planNum = planNum;
        this.dateN = DateN;
    }
}

