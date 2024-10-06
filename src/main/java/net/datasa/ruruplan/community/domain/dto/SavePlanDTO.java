package net.datasa.ruruplan.community.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private Integer boardNum;
    //저장한 회원 ID
    private String memberId;

    //작성자 닉네임
    private String nickName;

    //저장 일자
    private LocalTime saveTime;

    //플랜 제목
    private String planName;

    //게시글 내용
    private String contents;

    //좋아요 수
    private Integer likeCount;

    //작성일
    private LocalDateTime createDate;

    //수정일
    private LocalDateTime updateDate;

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
    //태그6
    private String tag6;

    //플랜 커버 이미지
    private String coverImageUrl;
}
