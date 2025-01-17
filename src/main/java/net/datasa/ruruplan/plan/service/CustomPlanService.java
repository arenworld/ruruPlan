package net.datasa.ruruplan.plan.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.gpt.domain.dto.GptCmdDTO;
import net.datasa.ruruplan.gpt.domain.entity.GptCmdEntity;
import net.datasa.ruruplan.gpt.repository.GptCmdRepository;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import net.datasa.ruruplan.member.repository.MemberRepository;
import net.datasa.ruruplan.plan.domain.dto.PlaceInfoDTO;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import net.datasa.ruruplan.plan.domain.dto.TaskDTO;
import net.datasa.ruruplan.plan.domain.entity.PlaceInfoEntity;
import net.datasa.ruruplan.plan.domain.entity.PlanEntity;
import net.datasa.ruruplan.plan.domain.entity.TaskEntity;
import net.datasa.ruruplan.plan.repository.*;
import net.datasa.ruruplan.plan.repository.jpa.PlaceInfoJpaRepository;
import net.datasa.ruruplan.plan.repository.jpa.PlanJpaRepository;
import net.datasa.ruruplan.plan.repository.jpa.TaskJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * GPT추천일정을 내 일정으로 담기 한 후 개별수정하는 컨트롤러
 * DB에 저장된 정보를 불러오고, 수정함
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CustomPlanService {

    private final GptCmdRepository cmdRepository;
    private final PlanJpaRepository planJpaRepository;
    private final TaskJpaRepository taskJpaRepository;
    private final TaskRepository taskRepository;
    private final PlaceInfoRepository placeInfoRepository;
    private final PlaceInfoJpaRepository placeInfoJpaRepository;
    private final MemberRepository memberRepository;


    /**
     * plan정보 가져오기 / 전체일정 / html에 연결되어 있는 getPlan메서드
     *
     * @return
     */
    public PlanDTO getPlan(Integer planNum) {
        //서버로부터 전달 받은 planNum으로 플랜 조회 (지금은 없으니까, 임의로 2를 넣음)
        PlanEntity planEntity = planJpaRepository.findById(planNum)
                .orElseThrow(() -> new EntityNotFoundException("플랜이 존재하지 않음"));

        //위에서 조회한 planEntity를 dto로 변환
        PlanDTO planDTO = convertToDTO(planEntity);

        Sort sort = Sort.by(Sort.Order.asc("dateN"), Sort.Order.asc("taskNum"));

        //taskList 처리
        List<TaskEntity> taskEntityList = taskJpaRepository.findByPlanPlanNum(planNum, sort);
        List<TaskDTO> taskDTOList = new ArrayList<>();

        for (TaskEntity taskEntity : taskEntityList) {
            TaskDTO taskDTO = convertToDTO(taskEntity);
            taskDTOList.add(taskDTO);
        }
        planDTO.setTaskList(taskDTOList);
        log.debug("taskList:{}", taskDTOList);
        return planDTO;
    }

    /**
     * 전체일정 및 일정별 대한 마커정보, 첫 로드시 allDay, 일자별 클릭시 일자별 리스크
     *
     * @param planNum
     * @return
     */
    public List<TaskDTO> getTaskList(Integer planNum, Integer dayNum) {
        Sort sort = Sort.by(Sort.Order.asc("dateN"), Sort.Order.asc("taskNum"));

        List<TaskEntity> taskEntityList = new ArrayList<>();

        // 전체일정 불러오기
        if (dayNum == 0) {
//            PlanEntity planEntity = planJpaRepository.findById(planNum)
//                    .orElseThrow(() -> new EntityNotFoundException("플랜없음"));
            taskEntityList = taskJpaRepository.findByPlanPlanNum(planNum, sort);
        } else {
            taskEntityList = taskRepository.dayTaskList(planNum, dayNum);
        }

        List<TaskDTO> dtoList = new ArrayList<>();
        for (TaskEntity taskEntity : taskEntityList) {
            TaskDTO dto = convertToDTO(taskEntity);
            dtoList.add(dto);
        }
        return dtoList;
    }


    /**
     * 테마별 장소정보(테마별 마커 및 상세정보)를 가져오는 메서드
     *
     * @param theme
     * @return
     */
    public List<PlaceInfoDTO> getThemeLocations(String theme) {

        switch (theme) {
            case "ショッピング" -> theme = "쇼핑";
            case "食べ物" -> theme = "식당";
            case "カフェ" -> theme = "카페";
            case "歴史" -> theme = "역사";
            case "文化" -> theme = "문화";
            case "ヒーリング" -> theme = "힐링";
            case "ランドマーク" -> theme = "랜드마크";
            case "体験" -> theme = "체험";
            case "レジャー" -> theme = "레포츠";
            case "子供" -> theme = "아이";
        }

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
     *
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
     * taskEntity, DTO변환 메서드, place객체를 직접 넣음
     *
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
                .contentsTypeKr(taskEntity.getContentsTypeKr())
                .contentsTypeJp(taskEntity.getContentsTypeJp())
                .cost(taskEntity.getCost())
                .build();
    }

    /**
     * placeEntity DTO 변환 메서드
     *
     * @param placeInfoEntity
     * @return
     */
    private PlaceInfoDTO convertToDTO(PlaceInfoEntity placeInfoEntity) {
        return PlaceInfoDTO.builder()
                .placeId(placeInfoEntity.getPlaceId())
                .titleKr(placeInfoEntity.getTitleKr())
                .titleJp(placeInfoEntity.getTitleJp())
                .addressKr(placeInfoEntity.getAddressKr())
                .addressJp(placeInfoEntity.getAddressJp())
                .mapX(placeInfoEntity.getMapX())
                .mapY(placeInfoEntity.getMapY())
                .siGunGu(placeInfoEntity.getSiGunGu())
                .contentsTypeKr(placeInfoEntity.getContentsTypeKr())
                .contentsTypeJp(placeInfoEntity.getContentsTypeJp())
                .theme1(placeInfoEntity.getTheme1())
                .theme2(placeInfoEntity.getTheme2())
                .theme3(placeInfoEntity.getTheme3())
                .petFriendly(placeInfoEntity.getPetFriendly())
                .barrierFree(placeInfoEntity.getBarrierFree())
                .overviewKr(placeInfoEntity.getOverviewKr())
                .overviewJp(placeInfoEntity.getOverviewJp())
                .heritage(placeInfoEntity.getHeritage())
                .infocenter(placeInfoEntity.getInfocenter())
                .usetimeKr(placeInfoEntity.getUsetimeKr())
                .usetimeJp(placeInfoEntity.getUsetimeJp())
                .restdateKr(placeInfoEntity.getRestdateKr())
                .restdateJp(placeInfoEntity.getRestdateJp())
                .fee(placeInfoEntity.getFee())
                .feeInfoKr(placeInfoEntity.getFeeInfoKr())
                .feeInfoJp(placeInfoEntity.getFeeInfo_Jp())
                .originImgUrl(placeInfoEntity.getOriginImgUrl())
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
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 명령어"));
        return convertToDTO(cmdEntity);
    }

    public void updateDuration(String newDurationHour, String newDurationMinute, Integer taskNum, Integer planNum, String newCost) {
        // 수정할 새로운 소요시간
        LocalTime newDuration = LocalTime.of(Integer.parseInt(newDurationHour), Integer.parseInt(newDurationMinute));

        // 소요시간을 업데이트 해야 하는 taskEntity를 불러옴
        TaskEntity thisTaskEntity = taskJpaRepository.findById(taskNum)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 태스크"));

        // 일단 바뀌어야 하는 일정의 소요시간을 바꾸고
        thisTaskEntity.setDuration(newDuration);
        thisTaskEntity.setCost(Integer.parseInt(newCost));

        // startTime을 수정해줘야 하는 entityList를 부를 조건을 설정
        int dayNum = thisTaskEntity.getDateN(); // 해당 일자의 여행만
        LocalTime fromPoint = thisTaskEntity.getStartTime(); // 지금 소요시간을 수정한 이 태스크 뒤에 오는 애들의 첫시간을 바꿔야함

        List<TaskEntity> updateEntityList = taskRepository.updateDurationList(planNum, dayNum, fromPoint);

        for (int i = 0; i < updateEntityList.size(); i++) {
            // 새로운 첫시간 = 바로 앞 일정의 첫시간 + 바로 앞 일정의 소요시간

            // 바로 앞 일정의 첫시간            
            LocalTime previousTaskStart;
            // 바로 앞 일정의 소요시간
            LocalTime previousTaskDuration;
            // 바로 앞 일정의 소요시간의 계산화
            Duration durationCal;

            // 첫번째 요소 즉; 소요시간이 변경된 바로 뒷 일정
            if (i == 0) {
                previousTaskStart = fromPoint; // 지금 이 일정에서
                durationCal = Duration.ofHours(newDuration.getHour()).plusMinutes(newDuration.getMinute()); // 바뀐 소요시간을 더하면 지금 이일정의 끝시간 = 다음 일정의 첫시간
            }

            // 두번째 요소부터는, 첫번째 요소들로 더해감. startTime[i] = startTime[i-1] + duration[i-1];
            else {
                previousTaskStart = updateEntityList.get(i - 1).getStartTime(); //
                previousTaskDuration = updateEntityList.get(i - 1).getDuration();
                durationCal = Duration.ofHours(previousTaskDuration.getHour()).plusMinutes(previousTaskDuration.getMinute());
            }

            LocalTime newStartTime = previousTaskStart.plus(durationCal);
            updateEntityList.get(i).setStartTime(newStartTime);

        }

    }

    public PlaceInfoDTO getPlaceInfo(String placeId) {
        PlaceInfoEntity placeEntity = placeInfoJpaRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("없는 장소"));
        log.debug("장소엔티티:{}", placeEntity);

        return convertToDTO(placeEntity);
    }

    public void updateTaskPlace(Integer planNum, Integer targetTaskNum, String newPlaceId, Integer preTaskNum
            , double preTransDuration, String preTransType, Integer nextTaskNum, double nextTransDuration, String nextTransType) {
        // 일정장소 수정 대상
        TaskEntity taskEntity = taskJpaRepository.findById(targetTaskNum)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 task"));

        // 바로 앞 이동태스크
        TaskEntity preTaskEntity = taskJpaRepository.findById(preTaskNum)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 task"));

        // 바로 뒤 이동태스크
        TaskEntity nextTaskEntity = taskJpaRepository.findById(nextTaskNum)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 task"));

        // 새로운 장소정보
        PlaceInfoEntity placeInfoEntity = placeInfoJpaRepository.findById(newPlaceId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 place"));

        // 앞 장소와의 새로운 이동거리
        LocalTime newPreTransDuration = convertToLocalTime(preTransDuration);

        // 뒤 장소와의 새로운 이동거리
        LocalTime newNextTransDuration = convertToLocalTime(nextTransDuration);

        // 정보 업데이트
        taskEntity.setPlace(placeInfoEntity);
        taskEntity.setCost(placeInfoEntity.getFee() == null ? 0 : placeInfoEntity.getFee());
        taskEntity.setContentsTypeKr(placeInfoEntity.getContentsTypeKr());
        taskEntity.setContentsTypeJp(placeInfoEntity.getContentsTypeJp());
        preTaskEntity.setDuration(newPreTransDuration);
        preTaskEntity.setContentsTypeKr(preTransType);
        preTaskEntity.setContentsTypeJp(preTransType.equals("도보") ? "徒歩" : "大衆交通");

        nextTaskEntity.setDuration(newNextTransDuration);

        // 바뀐 시간 table 정리;
        LocalTime fromPoint = preTaskEntity.getStartTime();
        List<TaskEntity> updateEntityList = taskRepository.updateDurationList(planNum, preTaskEntity.getDateN(), fromPoint);

        for (int i = 0; i < updateEntityList.size(); i++) {
            // 새로운 첫시간 = 바로 앞 일정의 첫시간 + 바로 앞 일정의 소요시간

            // 바로 앞 일정의 첫시간
            LocalTime previousTaskStart;
            // 바로 앞 일정의 소요시간
            LocalTime previousTaskDuration;
            // 바로 앞 일정의 소요시간의 계산화
            Duration durationCal;

            // 첫번째 요소 즉; 소요시간이 변경된 바로 뒷 일정
            if (i == 0) {
                previousTaskStart = fromPoint; // 지금 이 일정에서
                durationCal = Duration.ofHours(newPreTransDuration.getHour()).plusMinutes(newPreTransDuration.getMinute()); // 바뀐 소요시간을 더하면 지금 이일정의 끝시간 = 다음 일정의 첫시간
            }

            // 두번째 요소부터는, 첫번째 요소들로 더해감. startTime[i] = startTime[i-1] + duration[i-1];
            else {
                previousTaskStart = updateEntityList.get(i - 1).getStartTime(); //
                previousTaskDuration = updateEntityList.get(i - 1).getDuration();
                durationCal = Duration.ofHours(previousTaskDuration.getHour()).plusMinutes(previousTaskDuration.getMinute());
            }

            LocalTime newStartTime = previousTaskStart.plus(durationCal);
            updateEntityList.get(i).setStartTime(newStartTime);

        }

    }

    public LocalTime convertToLocalTime(double targetDuration) {
        int newHour = (int) Math.floor(targetDuration / 60);
        int newMinute = (int) Math.floor(targetDuration % 60);
        return LocalTime.of(newHour, newMinute);
    }

    public void updateFirstTaskPlace(Integer planNum, Integer targetTaskNum, String newPlaceId, Integer nextTaskNum, double nextTransDuration) {
        // 일정장소 수정 대상
        TaskEntity taskEntity = taskJpaRepository.findById(targetTaskNum)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 task"));

        // 바로 뒤 이동태스크
        TaskEntity nextTaskEntity = taskJpaRepository.findById(nextTaskNum)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 task"));

        // 새로운 장소정보
        PlaceInfoEntity placeInfoEntity = placeInfoJpaRepository.findById(newPlaceId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 place"));

        // 뒤 장소와의 새로운 이동거리
        LocalTime newNextTransDuration = convertToLocalTime(nextTransDuration);

        // 정보 업데이트
        taskEntity.setPlace(placeInfoEntity);
        taskEntity.setCost(placeInfoEntity.getFee() == null ? 0 : placeInfoEntity.getFee());
        taskEntity.setContentsTypeKr(placeInfoEntity.getContentsTypeKr());
        taskEntity.setContentsTypeJp(placeInfoEntity.getContentsTypeJp());
        nextTaskEntity.setDuration(newNextTransDuration);


        // 바뀐 시간 table 정리, 수정하고자 하는 taskEntity가 이날의 첫시작 시간
        LocalTime fromPoint = taskEntity.getStartTime();
        List<TaskEntity> updateEntityList = taskRepository.updateDurationList(planNum, taskEntity.getDateN(), fromPoint);

        for (int i = 0; i < updateEntityList.size(); i++) {
            // 새로운 첫시간 = 바로 앞 일정의 첫시간 + 바로 앞 일정의 소요시간

            // 바로 앞 일정의 첫시간
            LocalTime previousTaskStart;
            // 바로 앞 일정의 소요시간
            LocalTime previousTaskDuration;
            // 바로 앞 일정의 소요시간의 계산화
            Duration durationCal;

            // 첫번째 요소 즉; 소요시간이 변경된 바로 뒷 일정
            if (i == 0) {
                previousTaskStart = fromPoint; // 지금 이 일정에서
                //durationCal = Duration.ofHours(newPreTransDuration.getHour()).plusMinutes(newPreTransDuration.getMinute()); // 바뀐 소요시간을 더하면 지금 이일정의 끝시간 = 다음 일정의 첫시간
            }

            // 두번째 요소부터는, 첫번째 요소들로 더해감. startTime[i] = startTime[i-1] + duration[i-1];
            else {
                previousTaskStart = updateEntityList.get(i - 1).getStartTime(); //
                previousTaskDuration = updateEntityList.get(i - 1).getDuration();
                durationCal = Duration.ofHours(previousTaskDuration.getHour()).plusMinutes(previousTaskDuration.getMinute());
            }


        }


    }

    public void updateLastTaskPlace(Integer planNum, Integer targetTaskNum, String newPlaceId, Integer preTaskNum, double preTransDuration) {
        // 일정장소 수정 대상
        TaskEntity taskEntity = taskJpaRepository.findById(targetTaskNum)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 task"));

        // 바로 앞 이동태스크
        TaskEntity preTaskEntity = taskJpaRepository.findById(preTaskNum)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 task"));


        // 새로운 장소정보
        PlaceInfoEntity placeInfoEntity = placeInfoJpaRepository.findById(newPlaceId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 place"));

        // 앞 장소와의 새로운 이동거리
        LocalTime newPreTransDuration = convertToLocalTime(preTransDuration);


        // 정보 업데이트
        taskEntity.setPlace(placeInfoEntity);
        taskEntity.setCost(placeInfoEntity.getFee() == null ? 0 : placeInfoEntity.getFee());
        taskEntity.setContentsTypeKr(placeInfoEntity.getContentsTypeKr());
        taskEntity.setContentsTypeJp(placeInfoEntity.getContentsTypeJp());
        preTaskEntity.setDuration(newPreTransDuration);
        //nextTaskEntity.setDuration(newNextTransDuration);


        // 바뀐 시간 table 정리;
        LocalTime fromPoint = preTaskEntity.getStartTime();
        List<TaskEntity> updateEntityList = taskRepository.updateDurationList(planNum, preTaskEntity.getDateN(), fromPoint);

        for (int i = 0; i < updateEntityList.size(); i++) {
            // 새로운 첫시간 = 바로 앞 일정의 첫시간 + 바로 앞 일정의 소요시간

            // 바로 앞 일정의 첫시간
            LocalTime previousTaskStart;
            // 바로 앞 일정의 소요시간
            LocalTime previousTaskDuration;
            // 바로 앞 일정의 소요시간의 계산화
            Duration durationCal;

            // 첫번째 요소 즉; 소요시간이 변경된 바로 뒷 일정
            if (i == 0) {
                previousTaskStart = fromPoint; // 지금 이 일정에서
                durationCal = Duration.ofHours(newPreTransDuration.getHour()).plusMinutes(newPreTransDuration.getMinute()); // 바뀐 소요시간을 더하면 지금 이일정의 끝시간 = 다음 일정의 첫시간
            }

            // 두번째 요소부터는, 첫번째 요소들로 더해감. startTime[i] = startTime[i-1] + duration[i-1];
            else {
                previousTaskStart = updateEntityList.get(i - 1).getStartTime(); //
                previousTaskDuration = updateEntityList.get(i - 1).getDuration();
                durationCal = Duration.ofHours(previousTaskDuration.getHour()).plusMinutes(previousTaskDuration.getMinute());
            }

            LocalTime newStartTime = previousTaskStart.plus(durationCal);
            updateEntityList.get(i).setStartTime(newStartTime);

        }


    }

    public void addNewTask(String newPlaceId, double preTransDuration, String preTransType
            , Integer planNum, Integer dayNum, Integer lastTaskNum, String username) {
        TaskEntity lastTaskEntity = taskJpaRepository.findById(lastTaskNum)
                .orElseThrow(() -> new EntityNotFoundException("없는 일정"));

        PlanEntity planEntity = planJpaRepository.findById(planNum)
                .orElseThrow(() -> new EntityNotFoundException("없는 플랜"));

        MemberEntity memberEntity = memberRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("없는 유저"));

        PlaceInfoEntity newPlaceInfoEntity = placeInfoJpaRepository.findById(newPlaceId)
                .orElseThrow(() -> new EntityNotFoundException("없는 장소"));

        PlaceInfoDTO newPlaceInfoDTO = convertToDTO(newPlaceInfoEntity);

        TaskEntity newTransTaskEntity = TaskEntity.builder()
                .plan(planEntity)
                .place(newPlaceInfoEntity)
                .member(memberEntity)
                .dateN(dayNum)
                .startTime(lastTaskEntity.getEndTime())
                .duration(convertToLocalTime(preTransDuration))
                .contentsTypeKr(preTransType)
                .contentsTypeJp(preTransType.equals("도보") ? "徒歩" : "大衆交通")
                .cost(1500)
                .build();

        taskJpaRepository.save(newTransTaskEntity);

        LocalTime newPlanTaskStartTime = newTransTaskEntity.getStartTime()
                .plusHours(convertToLocalTime(preTransDuration).getHour())
                .plusMinutes(convertToLocalTime(preTransDuration).getMinute());

        TaskEntity newPlanTaskEntity = TaskEntity.builder()
                .plan(planEntity)
                .place(newPlaceInfoEntity)
                .member(memberEntity)
                .dateN(dayNum)
                .startTime(newPlanTaskStartTime)
                .duration(LocalTime.of(0, 0))
                .contentsTypeKr(newPlaceInfoDTO.getContentsTypeKr())
                .contentsTypeJp(newPlaceInfoDTO.getContentsTypeJp())
                .cost(newPlaceInfoDTO.getFee())
                .build();

        taskJpaRepository.save(newPlanTaskEntity);

    }

    public void deleteTask(Integer dayNum, Integer planNum) {
        Sort sort = Sort.by(Sort.Order.asc("dateN"), Sort.Order.asc("taskNum"));

        //taskList 처리
        List<TaskEntity> taskEntityList = taskRepository.dayTaskList(planNum, dayNum);

        if (taskEntityList.size() >= 2) {
            // 마지막에서 두 번째 TaskEntity를 가져옴
            TaskEntity lastMoveTask = taskEntityList.get(taskEntityList.size() - 2);
            TaskEntity lastTask = taskEntityList.get(taskEntityList.size() - 1);

            // 마지막에서 두 번째 태스크 삭제
            taskJpaRepository.deleteById(lastMoveTask.getTaskNum());
            taskJpaRepository.deleteById(lastTask.getTaskNum());
        } else {
            // 에러 처리: 리스트에 태스크가 2개 미만일 경우
            throw new IllegalStateException("태스크가 충분하지 않습니다.");
        }


    }
}
