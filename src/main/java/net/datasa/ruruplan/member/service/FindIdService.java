package net.datasa.ruruplan.member.service;

import lombok.RequiredArgsConstructor;
import net.datasa.ruruplan.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindIdService {
    private final MemberRepository memberRepository;
}
