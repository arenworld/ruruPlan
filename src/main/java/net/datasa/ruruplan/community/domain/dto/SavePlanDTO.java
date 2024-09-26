package net.datasa.ruruplan.community.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * 저장한(스크랩한) 플랜 DTO
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavePlanDTO {

    //저장된 번호
    private Integer saveNum;
    //게시글 번호
    private Integer planNum;
    //저장한 회원 ID
    private String memberId;
    //저장 일자
    private LocalTime saveTime;
}
