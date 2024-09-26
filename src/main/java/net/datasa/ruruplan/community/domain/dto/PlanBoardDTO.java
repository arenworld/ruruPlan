package net.datasa.ruruplan.community.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 플랜 공유 게시판 DTO
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanBoardDTO {

    //게시판 번호
    private Integer boardNum;

    //플랜번호
    private Integer planNum;

    //작성자 ID
    private String memberId;

    //플랜 제목
    private String planName;

    //게시글 내용
    private String contents;

    //조회수
    private Integer viewCount;

    //좋아요 수
    private Integer likeCount;

    //작성일
    private LocalDateTime createTime;

    //수정일
    private LocalDateTime updateTime;

    //태그1
    private String tag1;
    //태그2
    private String tag2;
    //태그3
    private String tag3;
    //태그4
    private String tag4;
    //태그5
    private String tag5;

    //플랜 커버 이미지
    private String coverImageUrl;

    //댓글 리스트
    private List<PlanBoardRplyDTO> replyList;
}
