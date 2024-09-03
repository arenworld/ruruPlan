package net.datasa.ruruplan.member.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 회원정보 Entity
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ruru_member")
@EntityListeners(AuditingEntityListener.class)  // 자동 시간 저장 2단계
public class MemberEntity {

    //회원 아이디
    @Id
    @Column(name ="member_id", length = 30)
    private String memberId;

    //회원 비밀번호
    @Column(name = "member_pw", length = 100, nullable = false)
    private String memberPw;

    //회원 이메일
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    //회원 나이
    @Column(name = "age", nullable = false)
    private Integer age;

    //회원 닉네임
    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    //가입일
    @CreatedDate
    @Column(name = "join_date", nullable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime joinDate;

    //회원정보 수정일
    @LastModifiedDate
    @Column(name = "update_date", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime updateDate;

    //탈퇴일
    @Column(name = "deactivation_date")
    private LocalDateTime deactivationDate;

    //계정상태
    @Column(name = "enabled", nullable = false, columnDefinition = "tinyint(1) check(enabled in (0, 1))")
    @ColumnDefault("1")
    private Boolean enabled;

    //지위
    @Column(name="role_name",nullable = false)
    @ColumnDefault("'ROLE_USER'")
    private String roleName;

    //프로필사진URL
    @Column(name="profile_image_url", length = 255)
    private String profileImageUrl;

    //유저 활성화 상태
    @Column(name = "member_status", length = 50, nullable = false, columnDefinition = "check(member_Status in ('actived','deactived','inactived','suspended'))")
    @ColumnDefault("'actived'")
    private String memberStatus;
    
    //최근 로그인
    @Column(name="last_login")
    private LocalDateTime lastLogin;

}
