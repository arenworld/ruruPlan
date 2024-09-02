package net.datasa.ruruplan.member.repository;

import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 회원정보 Repository
 */
@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, String> {
}
