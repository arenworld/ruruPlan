package net.datasa.ruruplan.community.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.community.domain.dto.PlanBoardDTO;
import net.datasa.ruruplan.community.domain.entity.PlanBoardEntity;
import net.datasa.ruruplan.community.repository.PlanBoardRepository;
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

    /**
     * 검색 후 지정한 한페이지 분량의 글 목록 조회
     * @param page        현재 페이지
     * @param pageSize    한 페이지당 글 수
     * @param searchType1  검색 대상 (title, contents, id)
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
                        searchType1, searchType1, searchType1, searchType1, pageable);            default :
                entityPage = boardRepo.findAll(pageable);     break;
        }

/*
        log.debug("조회된 결과 엔티티페이지 : {}", entityPage.getContent());
*/

        //entityPage의 각 요소들을 순회하면서 convertToDTO() 메소드로 전달하여 DTO로 변환하고
        //이를 다시 새로운 Page객체로 만든다.
        Page<PlanBoardDTO> planboardDTOPage = entityPage.map(this::convertToDTO);
        return planboardDTOPage;
    }
    /**
     * DB에서 조회한 게시글 정보인 BoardEntity 객체를 BoardDTO 객체로 변환
     * @param entity    게시글 정보 Entity 객체
     * @return          게시글 정보 DTO 개체
     */
    private PlanBoardDTO convertToDTO(PlanBoardEntity entity) {
        return PlanBoardDTO.builder()
                .boardNum(entity.getBoardNum())
                .memberId(entity.getMember() != null ? entity.getMember().getMemberId() : null)
                .planName(entity.getPlanName())
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
    }


}
