package net.datasa.ruruplan.plan.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="ruru_task")
public class TaskEntity {

    // 활동 별 번호(자동증가)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_num")
    private Integer taskNum;

    // 플랜번호 참조(같은 여행일정 내에서는 같은 번호)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_num", referencedColumnName = "plan_num")
    private PlanEntity plan;

    // 장소 고유번호 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", referencedColumnName = "place_id")
    private PlaceInfoEntity place;

    // 멤버 아이디 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private MemberEntity member;

    // 여행 중 며칠째?
    @Column(name = "date_n", nullable = false)
    private Integer dateN;

    // 활동 시작 시간
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    // 활동 시간
    @Column(name = "duration", nullable = false)
    private LocalTime duration;

    // 활동 끝나는 시간
    @Column(name = "end_time", insertable = false, updatable = false)
    private LocalTime endTime;

    // 활동명(택시, 도보, 대중교통, 식사, 관광, 숙소)
    @Column(name = "task", nullable = false)
    private String task;

    // 활동 비용
    @Column(name = "cost", nullable = false)
    private Integer cost;

    // 사용자 메모
    @Column(name = "memo", length = 1000)
    private String memo;
}

