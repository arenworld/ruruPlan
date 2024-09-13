package net.datasa.ruruplan.gpt.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import java.time.LocalDate;  // LocalDate import
import java.time.LocalTime;  // LocalTime import

/**
 * 여행 계획에 대해 질문 및 답변 정보를 저장하는 엔터티.
 * 사용자의 일정, 인원, 숙박 정보, 여행 테마 등을 관리
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="ruru_gpt_cmd")
public class GptCmdEntity {
    // 기본 키, 자동 증가
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cmd_num")
    private Integer cmdNum;

    // 여행 계획을 생성한 사용자의 정보를 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", referencedColumnName = "member_id")
    private MemberEntity member;

    // 한국에 도착하는 날짜
    @Column(name = "first_date", nullable = false)
    private LocalDate firstDate;

    // 한국을 떠나는 날짜
    @Column(name = "last_date", nullable = false)
    private LocalDate lastDate;

    // 몇박
    @Column(name = "nights", nullable = false)
    private Integer nights;

    // 며칠
    @Column(name = "days", nullable = false)
    private Integer days;

    // 입국 시간
    @Column(name = "arrival")
    private LocalTime arrival;

    // 출국 시간
    @Column(name = "depart")
    private LocalTime depart;

    // 여행의 유형(예: 혼자, 커플, 아이들, 부모님, 친구 등)
    @Column(name = "trip_type", nullable = false)
    private String tripType;

    // 어른 인원 수. 기본값은 1명
    @Column(name = "adult", nullable = false, columnDefinition = "integer default 1")
    private Integer adult;

    // 아이 인원 수 저장. 기본값은 0명.
    @Column(name = "children", nullable = false, columnDefinition = "integer default 0")
    private Integer children;

    // 첫 번째 여행 테마
    @Column(name = "theme_1", nullable = false)
    private String theme1;

    // 두 번째 여행 테마를 저장
    @Column(name = "theme_2", nullable = false)
    private String theme2;

    // 세 번째 여행 테마를 저장
    @Column(name = "theme_3", nullable = false)
    private String theme3;

    // 여행 일정의 밀도. 0은 널널, 1은 빽빽
    @Column(name = "density", nullable = false, columnDefinition = "tinyint(1) default 0 check(density in (0, 1))")
    private Boolean density;
}
