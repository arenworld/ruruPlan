package net.datasa.ruruplan.gpt.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;

import java.time.LocalDate;
import java.time.LocalTime;


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
    // 'member_id'로 MemberEntity와 다대일관계
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

    // 첫 번째 여행 테마의 가중치. 기본값은 2
    @Column(name = "theme_1_weight", nullable = false, columnDefinition = "integer default 2 check(theme_1_weight in (1, 2, 3))")
    private Integer theme1Weight;

    // 두 번째 여행 테마를 저장
    @Column(name = "theme_2", nullable = false)
    private String theme2;

    @Column(name = "theme_2_weight", nullable = false, columnDefinition = "integer default 2 check(theme_2_weight in (1, 2, 3))")
    private Integer theme2Weight;

    // 세 번째 여행 테마를 저장
    @Column(name = "theme_3", nullable = false)
    private String theme3;

    @Column(name = "theme_3_weight", nullable = false, columnDefinition = "integer default 2 check(theme_3_weight in (1, 2, 3))")
    private Integer theme3Weight;

    // 여행 일정의 밀도. 0은 널널, 1은 빽빽
    @Column(name = "density", nullable = false, columnDefinition = "tinyint(1) default 0 check(density in (0, 1))")
    private Boolean density;

    // 숙박 이동 여부 0은 한곳에만 숙박, 1은 여러곳에 숙박
    @Column(name = "move_status", nullable = false, columnDefinition = "tinyint(1) default 0 check(move_status in (0, 1))")
    private Boolean moveStatus;

    // 숙박 비용 범위. 0: 저렴, 1: 적당, 2: 호화, 3: 반반입니다.
    @Column(name = "room_cost", nullable = false, columnDefinition = "integer default 1 check(room_cost in (0, 1, 2, 3))")
    private Integer roomCost;

    // 호텔 숙박 여부. 0: 아니오, 1: 예
    @Column(name = "hotel", nullable = false, columnDefinition = "tinyint(1) default 0 check(hotel in (0, 1))")
    private Boolean hotel;

    // 호텔 숙박일수. 기본값은 0
    @Column(name = "hotel_nights", nullable = false, columnDefinition = "integer default 0")
    private Integer hotelNights;

    // 모텔 숙박 여부
    @Column(name = "motel", nullable = false, columnDefinition = "tinyint(1) default 0 check(motel in (0, 1))")
    private Boolean motel;

    // 모텔 숙박일수. 기본값은 0입니다.
    @Column(name = "motel_nights", nullable = false, columnDefinition = "integer default 0")
    private Integer motelNights;

    // 게스트하우스 숙박 여부
    @Column(name = "guesthouse", nullable = false, columnDefinition = "tinyint(1) default 0 check(guesthouse in (0, 1))")
    private Boolean guesthouse;

    // 게스트하우스 숙박일수. 기본값은 0
    @Column(name = "guesthouse_nights", nullable = false, columnDefinition = "integer default 0")
    private Integer guesthouseNights;

    // 한옥 숙박 여부
    @Column(name = "hanok", nullable = false, columnDefinition = "tinyint(1) default 0 check(hanok in (0, 1))")
    private Boolean hanok;

    // 한옥 숙박일수. 기본값은 0
    @Column(name = "hanok_nights", nullable = false, columnDefinition = "integer default 0")
    private Integer hanokNights;
}

