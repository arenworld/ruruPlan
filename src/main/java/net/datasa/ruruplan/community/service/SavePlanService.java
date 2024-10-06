package net.datasa.ruruplan.community.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.community.domain.dto.SavePlanDTO;
import net.datasa.ruruplan.community.domain.entity.SavePlanEntity;
import net.datasa.ruruplan.community.repository.SavePlanRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SavePlanService {
    final SavePlanRepository saveRepo;

    /**
     * 북마크 플랜 목록 조회
     * @return
     */
    public List<SavePlanDTO> getbookmarks(String userId) {
        //정렬
        Sort sort = Sort.by(Sort.Direction.DESC, "saveNum");
        //나의 플랜 리스트
        List<SavePlanEntity> entityList = saveRepo.findAllByMember_MemberId(userId, sort);

        log.debug("플랜목록 조회 : {}", entityList);

        List<SavePlanDTO> dtoList = new ArrayList<>();
        for (SavePlanEntity entity : entityList) {
            SavePlanDTO dto = SavePlanDTO.builder()
                    .boardNum(entity.getPlanBoard().getBoardNum())
                    .planName(entity.getPlanBoard().getPlanName())
                    .memberId(entity.getMember().getMemberId())
                    .nickName(entity.getPlanBoard().getMember().getNickname())
                    .saveNum(entity.getSaveNum())
                    .tag1(entity.getPlanBoard().getTag1())
                    .tag2(entity.getPlanBoard().getTag2())
                    .tag3(entity.getPlanBoard().getTag3())
                    .tag4(entity.getPlanBoard().getTag4())
                    .tag5(entity.getPlanBoard().getTag5())
                    .tag6(entity.getPlanBoard().getTag6())
                    .likeCount(entity.getPlanBoard().getLikeCount())
                    .coverImageUrl(entity.getPlanBoard().getPlan().getCoverImageUrl())
                    .build();
            dtoList.add(dto);
        }

        return dtoList;
    }
}
