package net.datasa.ruruplan.member.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원정보 DTO
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    //회원 아이디
    private String memberId;

    //회원 비밀번호
    private String memberPw;

    //회원 이메일
    private String email;

    //회원 나이
    private Integer age;

    //회원 닉네임
    private String nickname;

    //가입일
    private LocalDateTime joinDate;

    //회원정보 수정일
    private LocalDateTime updateDate;

    //탈퇴일
    private LocalDateTime deactivationDate;

    //계정상태
    private Boolean enabled;

    //지위
    private String roleName;

    //프로필사진URL
    private String profileImageUrl;

    //유저 활성화 상태
    private String memberStatus;

    //최근 로그인
    private LocalDateTime lastLogin;
}