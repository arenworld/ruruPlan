package net.datasa.ruruplan.plan.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import net.datasa.ruruplan.plan.domain.dto.TaskDTO;
import net.datasa.ruruplan.plan.domain.entity.PlanEntity;
import net.datasa.ruruplan.plan.domain.entity.TaskEntity;
import net.datasa.ruruplan.plan.repository.PlaceInfoRepository;
import net.datasa.ruruplan.plan.repository.PlanRepository;
import net.datasa.ruruplan.plan.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * plan정보 가져오기 / 전체일정
     * @return
     */
    public PlanDTO getPlan() {
        //서버로부터 전달 받은 planNum으로 플랜 조회 (지금은 없으니까, 임의로 2를 넣음)
        PlanEntity planEntity = planRepository.findById(2)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않음"));

        //위에서 조회한 planEntity를 dto로 변환
        PlanDTO planDTO = convertToDTO(planEntity);

        //taskList 처리
        List<TaskDTO> taskDTOList = new ArrayList<>();
        for (TaskEntity taskEntity : planEntity.getTaskList()) {
            TaskDTO taskDTO = convertToDTO(taskEntity);
            taskDTOList.add(taskDTO);
        }
        planDTO.setTaskList(taskDTOList);
        return planDTO;
    }

    //planEntity DTO변환 메서드
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

    //taskEntity, DTO변환 메서드
    private TaskDTO convertToDTO(TaskEntity taskEntity) {
        return TaskDTO.builder()
                .taskNum(taskEntity.getTaskNum())
                .planNum(taskEntity.getPlan().getPlanNum())
                .placeId(taskEntity.getPlace().getPlaceId())
                .memberId(taskEntity.getMember().getMemberId())
                .dateN(taskEntity.getDateN())
                .startTime(taskEntity.getStartTime())
                .duration(taskEntity.getDuration())
                .endTime(taskEntity.getEndTime())
                .task(taskEntity.getTask())
                .cost(taskEntity.getCost())
                .build();
    }
}
