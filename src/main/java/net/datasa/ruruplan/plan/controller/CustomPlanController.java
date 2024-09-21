package net.datasa.ruruplan.plan.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.gpt.domain.dto.GptCmdDTO;
import net.datasa.ruruplan.gpt.domain.entity.GptCmdEntity;
import net.datasa.ruruplan.plan.domain.dto.PlaceInfoDTO;
import net.datasa.ruruplan.plan.domain.dto.PlanAndThemeMarkers;
import net.datasa.ruruplan.plan.domain.dto.TaskDTO;
import net.datasa.ruruplan.plan.service.CustomPlanService;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GPT추천일정을 내 일정으로 담은 후 개별수정하는 컨트롤러
 * db에 이미 저장되어 있는 정보를 꺼내고, 다시 집어넣는 역할을 한다.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("custom")
public class CustomPlanController {

    private final CustomPlanService customPlanService;


    /**
     * 해당페이지 url 연결 메서드
     * 플랜번호만 하나 남기면 됨. (플랜 하나를 가져와서 출력하기 위한 메서드(html에 작성, origin용))
     * @param model
     * @return
     */
    @GetMapping("")
    public String customPlan(Model model) { //@RequestParam("planNum") Integer planNum) 지금은 테스트로 직접 넣고 나중에, planNum을 파람으로 받음
        // custom.html로드될 때 뿌려줄 플랜정보불러오기
        PlanDTO planDTO = customPlanService.getPlan();
        log.debug("cmdNum: {}", planDTO.getCmdNum());
        log.debug("planDTO: {}", planDTO);

        // 기존에 선택한 옵션사항을 보여주기 위한 cmdDTO 불러오기
        GptCmdDTO cmdDTO = customPlanService.getCmd(planDTO.getCmdNum());
        
        // start_date, end_date 활용해서 몇박 며칠인지 정의하는 기능 ChronoUnit은 날짜타입의 계산을 도와주는 객체
        long days = ChronoUnit.DAYS.between(planDTO.getStartDate(), planDTO.getEndDate()) + 1;

        String[] themeArray = {"쇼핑", "음식", "카페", "역사", "문화", "힐링", "랜드마크", "체험", "레포츠"};

        List<TaskDTO> taskList = planDTO.getTaskList();

        TaskDTO lastTaskDTO = planDTO.getTaskList().get(taskList.size()-1);
        Integer lastDay = lastTaskDTO.getDateN();
        model.addAttribute("days", days);
        model.addAttribute("planDTO", planDTO);
        model.addAttribute("themeArray", themeArray);
        model.addAttribute("lastDay", lastDay);
        model.addAttribute("cmdDTO", cmdDTO);
        log.debug("옵션정보:{}", cmdDTO);
        return "customView/customPlan";
    };


    /**
     * ajax용 일정표 그리기 / planNum으로 TaskList를 날짜별로 불러옴
     * 일자별 마커정보 / 첫로딩(allDay로 자동지정), 버튼클릭시 호출될 플랜정보
     * @param planNum
     * @param dayNum
     * @return
     */
    @ResponseBody
    @PostMapping("getPlan")
    public List<TaskDTO> getPlan(@RequestParam("planNum") Integer planNum, @RequestParam("dayNumOfButton") Integer dayNum) {
        return  customPlanService.getTaskList(planNum, dayNum);
    }

    /**
     * 테마별 / 테마 마커 출력시, 일자별 마커도 있어야 해서 같이 보냄
     * @param theme
     * @param planNum
     * @param dayNum
     * @return
     */
    @ResponseBody
    @PostMapping("themeMarkers")
    public PlanAndThemeMarkers themeMarker(@RequestParam("theme") String theme, @RequestParam("planNum") Integer planNum, @RequestParam("dayNumOfButton") Integer dayNum) {
        log.debug("입력받은 테마:{}", theme);
        List<PlaceInfoDTO> themeLocations = customPlanService.getThemeLocations(theme);
        List<TaskDTO> planLocations = customPlanService.getTaskList(planNum, dayNum);
        return new PlanAndThemeMarkers(themeLocations, planLocations);
    }


    /**
     * 소요시간, 비용 수정 메서드 (일단 시간이랑 비용만)
     * @param newDurationHour
     * @param newDurationMinute
     * @param taskNum
     * @param planNum
     * @param newCost
     */
    @ResponseBody
    @PostMapping("updateDuration")
    public void updateDuration(@RequestParam("newDurationHour") Integer newDurationHour, @RequestParam("newDurationMinute") Integer newDurationMinute, @RequestParam("taskNum") Integer taskNum, @RequestParam("planNum") Integer planNum
    , @RequestParam("newCost") Integer newCost) {

        customPlanService.updateDuration(newDurationHour, newDurationMinute, taskNum, planNum, newCost);
    }


}
