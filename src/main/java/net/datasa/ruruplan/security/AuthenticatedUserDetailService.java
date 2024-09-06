package net.datasa.ruruplan.security;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import net.datasa.ruruplan.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticatedUserDetailService implements UserDetailsService {

    private final BCryptPasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        MemberEntity memberEntity = memberRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("아이디가 없습니다."));

        //있으면 조회된 전보로 UserDetails객체 생성해서 리턴
        AuthenticatedUser user = AuthenticatedUser.builder()
                .id(memberEntity.getMemberId())
                .password(memberEntity.getMemberPw())
                .enabled(memberEntity.getEnabled())
                .roleName(memberEntity.getRoleName())
                .build();

        log.debug("인증정보 : {}", user);

        return user;
    }

}
