package net.datasa.ruruplan.gpt.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name="ruru_plan")
public class PlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_num")
    private Integer planNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cmd_num", referencedColumnName = "cmd_num")
    private GptCmdEntity gptCmd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private MemberEntity member;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @CreatedDate
    @Column(name = "plan_create_date", updatable = false)
    private LocalDateTime planCreateDate;

    @LastModifiedDate
    @Column(name = "plan_update_date")
    private LocalDateTime planUpdateDate;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    // plan 하나에 task여러개이므로...
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)	// orphanRemoval은 부모가 자식 참조 안하게되면 삭제
    private List<TaskEntity> taskList;
}