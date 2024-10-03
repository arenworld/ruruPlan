package net.datasa.ruruplan.myPage.service;

import net.datasa.ruruplan.member.domain.dto.MemberDTO;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import org.springframework.stereotype.Service;
import net.datasa.ruruplan.member.repository.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service

public class MyPageService {

    private final MemberRepository memberRepository;

    public MyPageService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
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
    @Transactional
    public void updateProfileImage(String username, String profileImageUrl) {
        MemberEntity member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        member.setProfileImageUrl(profileImageUrl);  // 이미지 경로 설정
        memberRepository.save(member);  // DB에 저장
    }

}
