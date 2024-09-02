package net.datasa.ruruplan.gpt.domain.dto;

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

    private Integer taskNum;
    private Integer planNum;
    private String placeId;
    private String memberId;
    private Integer dateN;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer duration;
    private String task;
    private Integer cost;
    private String memo;
}

