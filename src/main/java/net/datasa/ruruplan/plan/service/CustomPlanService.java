package net.datasa.ruruplan.plan.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.gpt.domain.dto.GptCmdDTO;
import net.datasa.ruruplan.gpt.domain.entity.GptCmdEntity;
import net.datasa.ruruplan.gpt.repository.GptCmdRepository;
import net.datasa.ruruplan.plan.domain.dto.PlaceInfoDTO;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import net.datasa.ruruplan.plan.domain.dto.TaskDTO;
import net.datasa.ruruplan.plan.domain.entity.PlaceInfoEntity;
import net.datasa.ruruplan.plan.domain.entity.PlanEntity;
import net.datasa.ruruplan.plan.domain.entity.TaskEntity;
import net.datasa.ruruplan.plan.repository.*;
import net.datasa.ruruplan.plan.repository.jpa.PlaceInfoJpaRepository;
import net.datasa.ruruplan.plan.repository.jpa.TaskJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GPT추천일정을 내 일정으로 담기 한 후 개별수정하는 컨트롤러
 *DB에 저장된 정보를 불러오고, 수정함
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomPlanService {

    private final GptCmdRepository cmdRepository;
    private final PlanRepository planRepository;
    private final TaskJpaRepository taskJpaRepository;
    private final TaskRepository taskRepository;
    private final PlaceInfoRepository placeInfoRepository;
    private final PlaceInfoJpaRepository placeInfoJpaRepository;


    /**
     * plan정보 가져오기 / 전체일정
     * @return
     */
    public PlanDTO getPlan() {
        //서버로부터 전달 받은 planNum으로 플랜 조회 (지금은 없으니까, 임의로 2를 넣음)
        PlanEntity planEntity = planRepository.findById(2)
                .orElseThrow(() -> new EntityNotFoundException("플랜이 존재하지 않음"));

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
     * 전체일정 및 일정별 대한 마커정보
     * @param planNum
     * @return
     */
    public List<TaskDTO> getPlanLocations(Integer planNum, Integer dayNum) {
        List<TaskEntity> taskEntityList = new ArrayList<>();

        // 전체일정 불러오기
        if (dayNum == 0) {
            Sort sort = Sort.by(Sort.Direction.ASC, "taskNum");
            PlanEntity planEntity = planRepository.findById(planNum)
                    .orElseThrow(() -> new EntityNotFoundException("플랜없음"));
            taskEntityList = planEntity.getTaskList();
        }

        else {
            taskEntityList = taskRepository.dayTaskList(planNum, dayNum);
        }

        List<TaskDTO> dtoList = new ArrayList<>();
        for(TaskEntity taskEntity : taskEntityList) {
            TaskDTO dto = convertToDTO(taskEntity);
            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     *
     * @param planNum
     * @param dayNum
     * @return
     */
    public List<TaskDTO> getDayTaskList(Integer planNum, Integer dayNum) {

        List<TaskEntity> taskEntityList;

        // 전체일정 불러오기
        if (dayNum == 0) {
            Sort sort = Sort.by(Sort.Direction.ASC, "taskNum");
            PlanEntity planEntity = planRepository.findById(planNum)
                    .orElseThrow(() -> new EntityNotFoundException("플랜없음"));
            taskEntityList = planEntity.getTaskList();
        }
        else {
            taskEntityList = taskRepository.dayTaskList(planNum, dayNum);
        }

        List<TaskDTO> dtoList = new ArrayList<>();

        for(TaskEntity taskEntity : taskEntityList) {
            TaskDTO dto = convertToDTO(taskEntity);
            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     * 일자별 일정에 대한 마커정보
     * @param planNum
     * @param dateNum
     * @return
     */
    public List<Map<String, Double>> getDayLocations(Integer planNum, Integer dateNum) {
        List<Map<String, Double>> dayLocations = new ArrayList<>();
        List<TaskEntity> taskEntityList = taskRepository.getDayLocations(planNum, dateNum);

        for (TaskEntity taskEntity : taskEntityList) {
            Map<String, Double> location = new HashMap<>();
            dayLocations.add(Map.of("lat", Double.parseDouble(taskEntity.getPlace().getMapY()),
                    "lng", Double.parseDouble(taskEntity.getPlace().getMapX()),
                    "taskNum", Double.parseDouble(String.valueOf(taskEntity.getTaskNum())),
                    "dateN", Double.parseDouble(String.valueOf(taskEntity.getDateN()))));
        }
        return dayLocations;
    }

    /**
     * 테마별 장소정보(테마별 마커 및 상세정보)를 가져오는 메서드
     * @param theme
     * @return
     */
    public List<PlaceInfoDTO> getThemeLocations(String theme) {
        List<PlaceInfoEntity> placeInfoEntityList = placeInfoRepository.findByTheme(theme);
        List<PlaceInfoDTO> placeInfoDTOList = new ArrayList<>();
        for (PlaceInfoEntity placeInfoEntity : placeInfoEntityList) {
            PlaceInfoDTO placeInfoDTO = convertToDTO(placeInfoEntity);
            placeInfoDTOList.add(placeInfoDTO);
        }
        return placeInfoDTOList;
    }


    /**
     * planEntity DTO변환 메서드
     * @param planEntity
     * @return
     */
    private PlanDTO convertToDTO(PlanEntity planEntity) {
        return PlanDTO.builder()
                .planNum(planEntity.getPlanNum())
                .cmdNum(planEntity.getCmd().getCmdNum())
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
        PlaceInfoEntity placeInfoEntity = placeInfoJpaRepository.findById(taskEntity.getPlace().getPlaceId())
                .orElseThrow(() -> new EntityNotFoundException("장소가 존재하지 않음"));

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
                .titleKor(placeInfoEntity.getTitleKor())
                .titleJpa(placeInfoEntity.getTitleJap())
                .addressKor(placeInfoEntity.getAddressKor())
                .addressJpa(placeInfoEntity.getAddressJap())
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

    private GptCmdDTO convertToDTO(GptCmdEntity cmdEntity) {
        return GptCmdDTO.builder()
                .cmdNum(cmdEntity.getCmdNum())
                .memberId(cmdEntity.getMember().getMemberId())
                .firstDate(cmdEntity.getFirstDate())
                .lastDate(cmdEntity.getLastDate())
                .nights(cmdEntity.getNights())
                .days(cmdEntity.getDays())
                .arrival(cmdEntity.getArrival())
                .depart(cmdEntity.getDepart())
                .tripType(cmdEntity.getTripType())
                .adult(cmdEntity.getAdult())
                .children(cmdEntity.getChildren())
                .theme1(cmdEntity.getTheme1())
                .theme2(cmdEntity.getTheme2())
                .theme3(cmdEntity.getTheme3())
                .density(cmdEntity.getDensity())
                .build();
    }

    public GptCmdDTO getCmd(Integer cmdNum) {
        GptCmdEntity cmdEntity = cmdRepository.findById(cmdNum)
                .orElseThrow(()-> new EntityNotFoundException("존재하지 않는 명령어"));
        return convertToDTO(cmdEntity);
    }
}
