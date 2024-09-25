package net.datasa.ruruplan.community.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import net.datasa.ruruplan.plan.domain.entity.PlanEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalTime;

/*@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="ruru_plan_board")
@@EntityListeners(AuditingEntityListener.class)*/
public class PlanBoardEntity {

    /*@Id
    @Column(name="board_num")
    private Integer boardNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="plan_num", referencedColumnName = "plan_num")
    private PlanEntity plan;

    @Column(name="contents")
    private String contents;

    @Column(name="view_count")
    private Integer viewCount;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name="create_date")
    private LocalTime createDate;

    @Column(name="update_date")
    private LocalTime updateDate;

    @Column(name = "tag1")
    private String tag1;

    @Column(name = "tag2")
    private String tag2;

    @Column(name = "tag3")
    private String tag3;

    @Column(name = "tag4")
    private String tag4;

    @Column(name = "tag5")
    private String tag5;*/

}
