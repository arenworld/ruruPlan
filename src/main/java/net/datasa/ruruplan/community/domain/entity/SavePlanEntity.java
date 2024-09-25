package net.datasa.ruruplan.community.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalTime;

/*@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ruru_save_plan")
@EntityListeners(AuditingEntityListener.class)*/
public class SavePlanEntity {

   /* @Id
    @Column(name="save_num")
    private Integer saveNum;

    @Column(name = "board_num")
    private Integer boardNum; //이거 아님

    @Column(name = "member_id")
    private Integer memberId; // 아님

    @Column(name = "save_date")
    private LocalTime saveDate;*/
}
