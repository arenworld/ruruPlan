package net.datasa.ruruplan.community.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 게시글 정보 DTO
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {
    private Integer boardNum;                       //게시글 일련번호
    private String memberId;                        //작성자 아이디
    private String nickname;                      //작성자 이름
    private String contents;                        //글 내용
    private Integer viewCount;                      //조회수
    private Integer likeCount;                      //추천수
    private String originalName;                    //첨부파일의 원래 이름
    private String fileName;                        //첨부파일의 저장된 이름
    private LocalDateTime createDate;               //작성 시간
    private LocalDateTime updateDate;               //수정 시간
    private List<ReplyDTO> replyList = new ArrayList<>(); // 기본값으로 빈 리스트 설정
    private String profileImageUrl;
}