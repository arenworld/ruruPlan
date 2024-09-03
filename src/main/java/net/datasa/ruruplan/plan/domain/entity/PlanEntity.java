package net.datasa.ruruplan.plan.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.ruruplan.gpt.domain.entity.GptCmdEntity;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 여행 계획(Plan) Entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name="ruru_plan")
public class PlanEntity {
    // plan 고유번호(자동증가)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_num")
    private Integer planNum;

    // plan 이름 (저장 시 사용자 설정)
    @Column(name = "plan_name", nullable=false)
    private String planName;

    // 질문번호 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cmd_num", referencedColumnName = "cmd_num")
    private GptCmdEntity gptCmd;

    // 멤버 아이디 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private MemberEntity member;

    // 여행 시작일
    @Column(name = "start_date")
    private LocalDate startDate;

    // 여행 끝나는 날
    @Column(name = "end_date")
    private LocalDate endDate;

    // 사용자가 질문에서 선택한 테마 1
    @Column(name = "theme1", nullable = false)
    private String theme1;

    // 사용자가 질문에서 선택한 테마 2
    @Column(name = "theme2", nullable = false)
    private String theme2;

    // 사용자가 질문에서 선택한 테마 3
    @Column(name = "theme3", nullable = false)
    private String theme3;

    // 계획 만들어진 시간
    @CreatedDate
    @Column(name = "plan_create_date", updatable = false)
    private LocalDateTime planCreateDate;

    // 계획 수정 시간
    @LastModifiedDate
    @Column(name = "plan_update_date")
    private LocalDateTime planUpdateDate;

    // 마이페이지에 보여질 플랜 커버 사진?
    @Column(name = "cover_image_url")
    private String coverImageUrl;

    // plan 하나에 task여러개이므로...
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)	// orphanRemoval은 부모가 자식 참조 안하게되면 삭제
    private List<TaskEntity> taskList;

}