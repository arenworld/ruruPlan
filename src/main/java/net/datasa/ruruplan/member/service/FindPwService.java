package net.datasa.ruruplan.member.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import net.datasa.ruruplan.member.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindPwService {
    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public String findPw(String id) {
        String email = memberRepository.findById(id).orElse(null).getEmail();
        return email;
    }

    public void resetPw(String id, String pw) {
        MemberEntity memberEntity = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("아이디가 없습니다."));

        memberEntity.setMemberPw(bCryptPasswordEncoder.encode(pw));
        memberRepository.save(memberEntity);
    }
}
