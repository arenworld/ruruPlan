package net.datasa.ruruplan.community.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * 플랜 공유 게시판 댓글 DTO
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanBoardRplyDTO {

    //댓글번호
    private Integer replyNum;
    //게시글 번호
    private Integer boardNum;
    //글쓴이 ID
    private String memberId;
    //댓글
    private String content;
    //생성일
    private LocalTime createTime;
}
