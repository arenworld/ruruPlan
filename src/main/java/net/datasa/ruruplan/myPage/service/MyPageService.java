package net.datasa.ruruplan.myPage.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.member.domain.dto.MemberDTO;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import net.datasa.ruruplan.plan.domain.entity.PlanEntity;
import net.datasa.ruruplan.plan.repository.jpa.PlanJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import net.datasa.ruruplan.member.repository.MemberRepository;

import java.util.List;
import java.util.ArrayList;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@Slf4j
@Transactional
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PlanJpaRepository planJpaRepository;

    public MyPageService(MemberRepository memberRepository, PlanJpaRepository planJpaRepository) {
        this.memberRepository = memberRepository;
        this.planJpaRepository = planJpaRepository;
    }

    public MemberDTO getMemberInfo(String username) {
        MemberEntity member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("user가 존재하지 않음"));
        // MemberEntity -> MemberDTO로 변환 (빌더 패턴 사용)
        MemberDTO dto = MemberDTO.builder()
                .memberId(member.getMemberId())
                .memberPw(member.getMemberPw())
                .email(member.getEmail())
                .age(member.getAge())
                .nickname(member.getNickname())
                .joinDate(member.getJoinDate())
                .updateDate(member.getUpdateDate())
                .deactivationDate(member.getDeactivationDate())
                .enabled(member.getEnabled())
                .roleName(member.getRoleName())
                .profileImageUrl(member.getProfileImageUrl())
                .memberStatus(member.getMemberStatus())
                .lastLogin(member.getLastLogin())
                .build();

        return dto;
    }

    public void updateProfileImage(String username, String profileImageUrl) {
        MemberEntity member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        member.setProfileImageUrl(profileImageUrl);  // 이미지 경로 설정
        memberRepository.save(member);  // DB에 저장
    }

    /**
     * 마이 플랜 목록 조회
     * @return
     */
    public List<PlanDTO> getMyPlanList(String userId) {
        //정렬
        Sort sort = Sort.by(Sort.Direction.DESC, "planNum");
        //나의 플랜 리스트
        List<PlanEntity> entityList = planJpaRepository.findAllByMember_MemberId(userId, sort);

        log.debug("플랜목록 조회 : {}", entityList);

        List<PlanDTO> dtoList = new ArrayList<>();
        for (PlanEntity entity : entityList) {
            PlanDTO dto = PlanDTO.builder()
                    .planNum(entity.getPlanNum())
                    .planName(entity.getPlanName())
                    .memberId(entity.getMember().getMemberId())
                    .startDate(entity.getStartDate())
                    .endDate(entity.getEndDate())
                    .planCreateDate(entity.getPlanCreateDate())
                    .planUpdateDate(entity.getPlanUpdateDate())
                    .coverImageUrl(entity.getCoverImageUrl())
                    .theme1(entity.getTheme1())
                    .theme2(entity.getTheme2())
                    .theme3(entity.getTheme3())
                    .build();
            dtoList.add(dto);
        }

        return dtoList;
    }

}
