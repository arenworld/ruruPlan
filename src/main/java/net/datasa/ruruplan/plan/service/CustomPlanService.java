package net.datasa.ruruplan.plan.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.plan.domain.dto.PlaceInfoDTO;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import net.datasa.ruruplan.plan.domain.dto.TaskDTO;
import net.datasa.ruruplan.plan.domain.entity.PlaceInfoEntity;
import net.datasa.ruruplan.plan.domain.entity.PlanEntity;
import net.datasa.ruruplan.plan.domain.entity.TaskEntity;
import net.datasa.ruruplan.plan.repository.PlaceInfoRepository;
import net.datasa.ruruplan.plan.repository.PlanRepository;
import net.datasa.ruruplan.plan.repository.TaskRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.*;

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

    /**
     * planEntity DTO변환 메서드
     * @param planEntity
     * @return
     */
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

    /**
     *taskEntity, DTO변환 메서드, place객체를 직접 넣음 
     * @param taskEntity
     * @return
     */
    private TaskDTO convertToDTO(TaskEntity taskEntity) {
        PlaceInfoEntity placeInfoEntity = placeInfoRepository.findById(taskEntity.getPlace().getPlaceId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않음"));

        PlaceInfoDTO placeInfoDTO = convertToDTO(placeInfoEntity);

        return TaskDTO.builder()
                .taskNum(taskEntity.getTaskNum())
                .planNum(taskEntity.getPlan().getPlanNum())
                .place(placeInfoDTO)
                .memberId(taskEntity.getMember().getMemberId())
                .dateN(taskEntity.getDateN())
                .startTime(taskEntity.getStartTime())
                .duration(taskEntity.getDuration())
                .endTime(taskEntity.getEndTime())
                .task(taskEntity.getTask())
                .cost(taskEntity.getCost())
                .build();
    }

    /**
     * placeEntity DTO 변환 메서드
     * @param placeInfoEntity
     * @return
     */
    private PlaceInfoDTO convertToDTO(PlaceInfoEntity placeInfoEntity) {
        return PlaceInfoDTO.builder()
                .placeId(placeInfoEntity.getPlaceId())
                .title(placeInfoEntity.getTitle())
                .address(placeInfoEntity.getAddress())
                .mapX(placeInfoEntity.getMapX())
                .mapY(placeInfoEntity.getMapY())
                .siGunGu(placeInfoEntity.getSiGunGu())
                .contentsType(placeInfoEntity.getContentsType())
                .theme1(placeInfoEntity.getTheme1())
                .theme2(placeInfoEntity.getTheme2())
                .theme3(placeInfoEntity.getTheme3())
                .petFriendly(placeInfoEntity.getPetFriendly())
                .barrierFree(placeInfoEntity.getBarrierFree())
                .build();
    }

    public List<Map<String, Double>> getLocationsAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "placeId");
        List<PlaceInfoEntity> placeInfoEntityList = placeInfoRepository.findAll(sort);

        List<Map<String, Double>> locationsAll = new ArrayList<>();

        for (PlaceInfoEntity placeInfoEntity : placeInfoEntityList) {
            Map<String, Double> location = new HashMap<>();
            locationsAll.add(Map.of("lat", Double.parseDouble(placeInfoEntity.getMapY()), "lng", Double.parseDouble(placeInfoEntity.getMapX())));
        }
        return locationsAll;
    }

    public List<Map<String, Double>> getPlanLocations(Integer planNum) {
        Sort sort = Sort.by(Sort.Direction.ASC, "taskNum");
        List<TaskEntity> taskEntityList = taskRepository.findByPlanPlanNum(planNum, sort);

        List<Map<String, Double>> planLocations = new ArrayList<>();

        for (TaskEntity taskEntity : taskEntityList) {
            Map<String, Double> location = new HashMap<>();
            planLocations.add(Map.of("lat", Double.parseDouble(taskEntity.getPlace().getMapY()),
                    "lng", Double.parseDouble(taskEntity.getPlace().getMapX()),
                    "taskNum", Double.parseDouble(String.valueOf(taskEntity.getTaskNum())),
                    "dateN", Double.parseDouble(String.valueOf(taskEntity.getDateN()))));
        }
        return planLocations;
    }
}
