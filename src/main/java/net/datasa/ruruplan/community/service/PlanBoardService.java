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

import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlanBoardService {
    final PlanBoardRepository planBoardRepository;
    final PlanJpaRepository planRepo;
    final MemberRepository memberRepo;



    public Page<PlanBoardDTO> getListByTags(int page, int pageSize, List<String> tags) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        // Entity로 조회된 결과를 DTO로 변환
        Page<PlanBoardEntity> planBoardEntities = planBoardRepository.findByTags(tags, pageable);

        Page<PlanBoardDTO> dtoPage = planBoardEntities.map(this::convertToDTO);

        return dtoPage;
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
                .tag6(entity.getTag6())
                .build();

        return dto;
    }

    /**
     * 마이페이지에서 공유
     * @param dto
     */
    public void sharePlan(Integer planNum, String userId) {
        PlanEntity plan = planRepo.findById(planNum)  // planNum으로 PlanEntity 조회
                .orElseThrow(() -> new EntityNotFoundException("Plan not found"));

        MemberEntity member = memberRepo.findById(userId)  // memberId로 MemberEntity 조회
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        int month = plan.getStartDate().getMonthValue();
        String season = "";
        String days = ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate()) + 1 + "days";

        //tag1
        switch (month) {
            case 3:
            case 4:
            case 5:
                season = "spring";
                break;
            case 6:
            case 7:
            case 8:
                season = "summer";
                break;
            case 9:
            case 10:
            case 11:
                season = "fall";
                break;
            case 12:
            case 1:
            case 2:
                season = "winter";
                break;
            default:
                break;
        }
        //tag3 누구랑
        String whom = plan.getCmd().getTripType();
        switch (whom) {
            case "혼자":
                whom = "trip_type1";
                break;
            case "커플":
                whom = "trip_type2";
                break;
            case "부모님":
                whom = "trip_type3";
                break;
            case "친구":
                whom = "trip_type4";
                break;
            case "아이":
                whom = "kids";
                break;
            default:
                break;

        }

        PlanBoardEntity entity = PlanBoardEntity.builder()
                .plan(plan)
                .member(member)
                .planName(plan.getPlanName())
                .viewCount(0)
                .likeCount(0)
                .tag1(season)
                .tag2(days)
                .tag3(whom)
                .tag4(plan.getTheme1())
                .tag5(plan.getTheme2())
                .tag6(plan.getTheme3())
                .build();

        planBoardRepository.save(entity);
    }

}
