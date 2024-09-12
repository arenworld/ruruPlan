package net.datasa.ruruplan.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import net.datasa.ruruplan.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FindIdService {
    private final MemberRepository memberRepository;

    public String findId(String email){

        return memberRepository.findByEmail(email)
                .map(MemberEntity::getMemberId)  // 회원이 존재할 경우 해당 아이디 반환
                .orElse(email);                  // 존재 안 할 경우 입력받은 이메일 반환ㄴ
    }
}
