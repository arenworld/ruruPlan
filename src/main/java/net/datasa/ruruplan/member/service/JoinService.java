package net.datasa.ruruplan.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.member.domain.dto.MemberDTO;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import net.datasa.ruruplan.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JoinService {
    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 아이디 중복확인
     * @param id
     * @return
     */
    public boolean idDuplicate(String id) {
        boolean res = memberRepository.existsById(id);
        return res;
    }

    /**
     * 회원가입 진행
     * @param member 입력받은
     */
    public void join(MemberDTO member) {
        MemberEntity memberEntity = MemberEntity.builder()
                .memberId(member.getMemberId())
                .memberPw(bCryptPasswordEncoder.encode(member.getMemberPw()))
                .email(member.getEmail())
                .nickname(member.getNickname())
                .age(member.getAge())
                .enabled(true)
                .roleName("ROLE_USER")
                .memberStatus("ACTIVED")
                .build();

        memberRepository.save(memberEntity);
    }

}
