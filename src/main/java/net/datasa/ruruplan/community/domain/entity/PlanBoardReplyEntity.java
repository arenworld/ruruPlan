package net.datasa.ruruplan.community.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 플랜 공유 게시판 댓글 Entity
 */
@Builder
@Getter
@Setter
@ToString(exclude = "planBoard")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ruru_plan_board_reply")
@EntityListeners(AuditingEntityListener.class)
public class PlanBoardReplyEntity {

    //댓글 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_num")
    private Integer replyNum;

    //게시판 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_num", referencedColumnName = "board_num")
    private PlanBoardEntity planBoard;

    //글쓴이 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private MemberEntity member;

    //내용
    @Column(name = "contents", length = 2000, nullable = false)
    private String content;

    //생성일
    @Column(name = "create_date", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createDate;

}
