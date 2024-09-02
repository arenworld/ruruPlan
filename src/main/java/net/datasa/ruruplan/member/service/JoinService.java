package net.datasa.ruruplan.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
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
