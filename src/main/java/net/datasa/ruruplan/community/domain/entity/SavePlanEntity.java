package net.datasa.ruruplan.community.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 저장(스크랩)한 플랜 Entity
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ruru_save_plan")
@EntityListeners(AuditingEntityListener.class)
public class SavePlanEntity {

    //저장한 플랜 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="save_num")
    private Integer saveNum;

    //게시판 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_num", referencedColumnName = "board_num")
    private PlanBoardEntity planBoard;

    //저장한 회원아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private MemberEntity member;

    //저장한 일자
    @Column(name = "save_date", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime saveDate;
}
