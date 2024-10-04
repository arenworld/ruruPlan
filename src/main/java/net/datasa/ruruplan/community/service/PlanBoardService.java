package net.datasa.ruruplan.community.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.community.domain.dto.PlanBoardDTO;
import net.datasa.ruruplan.community.domain.entity.PlanBoardEntity;
import net.datasa.ruruplan.community.repository.PlanBoardRepository;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import net.datasa.ruruplan.member.repository.MemberRepository;
import net.datasa.ruruplan.plan.domain.entity.PlanEntity;
import net.datasa.ruruplan.plan.repository.impl.PlanRepositoryImpl;
import net.datasa.ruruplan.plan.repository.jpa.PlanJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlanBoardService {
    final PlanBoardRepository boardRepo;
    final PlanJpaRepository planRepo;
    final MemberRepository memberRepo;

    /**
     * 플랜 공유 게시판 목록
     * @param page        현재 페이지
     * @param pageSize    한 페이지당 글 수
     * @param searchType1  검색 대상
     * @param searchType2  검색어
     * @return 한페이지의 글 목록
     */
    public Page<PlanBoardDTO> getList(int page, int pageSize, String searchType1, String searchType2) {
        //Page 객체는 번호가 0부터 시작
        page--;

        //페이지 조회 조건 (현재 페이지, 페이지당 글수, 정렬 순서, 정렬 기준 컬럼)
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "boardNum");

        Page<PlanBoardEntity> entityPage = null;

        switch (searchType1) {
            case "theme5" :
                entityPage = boardRepo.findByTag1ContainingOrTag2ContainingOrTag3ContainingOrTag4ContainingOrTag5Containing(searchType1,
                        searchType1, searchType1, searchType1, searchType1, pageable);
            default :
                entityPage = boardRepo.findAll(pageable); break;
        }

        log.debug("조회된 결과 엔티티페이지 : {}", entityPage.getContent());

        //entityPage의 각 요소들을 순회하면서 convertToDTO() 메소드로 전달하여 DTO로 변환하고
        //이를 다시 새로운 Page객체로 만든다.
        Page<PlanBoardDTO> planboardDTOPage = entityPage.map(this::convertToDTO);
        return planboardDTOPage;
    }

    /**
     * DB에서 조회한 게시글 정보인 PlanBoardEntity 객체를 BoardDTO 객체로 변환
     * @param entity    게시글 정보 Entity 객체
     * @return          게시글 정보 DTO 개체
     */
    private PlanBoardDTO convertToDTO(PlanBoardEntity entity) {
        PlanBoardDTO dto = new PlanBoardDTO().builder()
                .boardNum(entity.getBoardNum())
                .memberId(entity.getMember() != null ? entity.getMember().getMemberId() : null)
                .nickName(entity.getMember() != null ? entity.getMember().getNickname() : null)
                .planNum(entity.getPlan() != null ? entity.getPlan().getPlanNum() : null)
                .planName(entity.getPlan() != null ? entity.getPlan().getPlanName() : null)
                .contents(entity.getContents())
                .coverImageUrl(entity.getPlan().getCoverImageUrl())
                .viewCount(entity.getViewCount())
                .likeCount(entity.getLikeCount())
                .createDate(entity.getCreateDate())
                .updateDate(entity.getUpdateDate())
                .tag1(entity.getTag1())
                .tag2(entity.getTag2())
                .tag3(entity.getTag3())
                .tag4(entity.getTag4())
                .tag5(entity.getTag5())
                .build();

        return dto;
    }

    public void save(PlanBoardDTO dto) {
        PlanEntity plan = planRepo.findById(dto.getPlanNum())  // planNum으로 PlanEntity 조회
                .orElseThrow(() -> new EntityNotFoundException("Plan not found"));

        MemberEntity member = memberRepo.findById(dto.getMemberId())  // memberId로 MemberEntity 조회
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        PlanBoardEntity entity = PlanBoardEntity.builder()
                .plan(plan)
                .member(member)
                .planName(dto.getPlanName())
                .contents(dto.getContents())
                .viewCount(0)
                .likeCount(0)
                .tag1(dto.getTag1())
                .tag2(dto.getTag2())
                .tag3(dto.getTag3())
                .tag4(dto.getTag4())
                .tag5(dto.getTag5())
                .build();

        // 플랜 제목과 커버사진이 변경되었다면 적용.
        plan.setPlanName(dto.getPlanName());
        plan.setCoverImageUrl(dto.getCoverImageUrl());

        planRepo.save(plan);

        boardRepo.save(entity);
    }

}
