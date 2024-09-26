package net.datasa.ruruplan.community.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import net.datasa.ruruplan.plan.domain.entity.PlanEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 플랜 공유 게시판 Entity
 */
@Builder
@Getter
@Setter
@ToString(exclude = "plan")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="ruru_plan_board")
@EntityListeners(AuditingEntityListener.class)
public class PlanBoardEntity {

    //게시글 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="board_num")
    private Integer boardNum;

    //작성자 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private MemberEntity member;

    //공유 플랜 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="plan_num", referencedColumnName = "plan_num", nullable = false)
    private PlanEntity plan;

    //공유 플랜 이름
    @JoinColumn(name="plan_num", referencedColumnName = "plan_num", nullable = false)
    private String planName;

    //글내용
    @Column(name="contents")
    private String contents;

    //조회수
    @Column(name="view_count", columnDefinition = "default 0")
    private Integer viewCount;

    //좋아요 수
    @Column(name = "like_count", columnDefinition = "default 0")
    private Integer likeCount;

    //작성일
    @Column(name="create_date" , columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createDate;

    //수정일
    @Column(name="update_date", columnDefinition = "timestamp default current_timestamp on update current_timstamp")
    private LocalDateTime updateDate;

    //태그1
    @Column(name = "tag1", length = 30)
    private String tag1;

    //태그2
    @Column(name = "tag2", length = 30)
    private String tag2;

    //태그3
    @Column(name = "tag3", length = 30)
    private String tag3;

    //태그4
    @Column(name = "tag4", length = 30)
    private String tag4;

    //태그5
    @Column(name = "tag5", length = 30)
    private String tag5;

}
