package net.datasa.ruruplan.member.service;

import lombok.RequiredArgsConstructor;
import net.datasa.ruruplan.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {
    private final MemberRepository memberRepository;

    /**
     * 아이디 중복확인
     * @param id
     * @return
     */
    public boolean idDuplicate(String id) {
        boolean res = memberRepository.existsById(id);
        return res;
    }

}
