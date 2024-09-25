package net.datasa.ruruplan.community.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 플랜 공유 게시판 DTO
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanBoardDTO {

    private int boardNum;

}
