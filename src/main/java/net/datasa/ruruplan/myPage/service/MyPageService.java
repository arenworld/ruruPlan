package net.datasa.ruruplan.myPage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.member.domain.dto.MemberDTO;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import net.datasa.ruruplan.plan.domain.entity.PlanEntity;
import net.datasa.ruruplan.plan.repository.PlanRepository;
import net.datasa.ruruplan.security.AuthenticatedUser;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import net.datasa.ruruplan.member.repository.MemberRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@Slf4j
@Transactional
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;

    public MyPageService(MemberRepository memberRepository, PlanRepository planRepository) {
        this.memberRepository = memberRepository;
        this.planRepository = planRepository;
    }

    public List<MemberDTO> getMemberInfo() {
        List<MemberEntity> members = memberRepository.findAll();
        // MemberEntity -> MemberDTO로 변환 (빌더 패턴 사용)
        return members.stream()
                .map(member -> MemberDTO.builder()
                        .memberId(member.getMemberId())
                        .nickname(member.getNickname())
                        .email(member.getEmail())
                        .profileImageUrl(member.getProfileImageUrl())
                        .build())
                .collect(Collectors.toList());
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
        List<PlanEntity> entityList = planRepository.findAllByMember_MemberId(userId, sort);

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
