package net.datasa.ruruplan.plan.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import net.datasa.ruruplan.plan.domain.entity.PlanEntity;
import net.datasa.ruruplan.plan.repository.PlaceInfoRepository;
import net.datasa.ruruplan.plan.repository.PlanRepository;
import net.datasa.ruruplan.plan.repository.TaskRepository;
import org.springframework.stereotype.Service;

/**
 * GPT추천일정을 내 일정으로 담기 한 후 개별수정하는 컨트롤러
 *DB에 저장된 정보를 불러오고, 수정함
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomPlanService {

    private final PlanRepository planRepository;
    private final TaskRepository taskRepository;
    private final PlaceInfoRepository placeInfoRepository;

    public PlanDTO getPlan() {
        PlanEntity planEntity = planRepository.findById(2)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않음"));
        PlanDTO planDTO = convertToDTO(planEntity);
        return planDTO;
    }

    private PlanDTO convertToDTO(PlanEntity planEntity) {
        return PlanDTO.builder()
                .planNum(planEntity.getPlanNum())
                .memberId(planEntity.getMember().getMemberId())
                .planName(planEntity.getPlanName())
                .startDate(planEntity.getStartDate())
                .endDate(planEntity.getEndDate())
                .planCreateDate(planEntity.getPlanCreateDate())
                .planUpdateDate(planEntity.getPlanUpdateDate())
                .theme1(planEntity.getTheme1())
                .theme2(planEntity.getTheme2())
                .theme3(planEntity.getTheme3())
                .build();
    }
}
