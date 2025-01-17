package net.datasa.ruruplan.plan.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.exchangeTest;
import net.datasa.ruruplan.gpt.domain.dto.GptCmdDTO;
import net.datasa.ruruplan.gpt.domain.entity.GptCmdEntity;
import net.datasa.ruruplan.plan.domain.dto.PlaceInfoDTO;
import net.datasa.ruruplan.plan.domain.dto.PlanAndThemeMarkers;
import net.datasa.ruruplan.plan.domain.dto.TaskDTO;
import net.datasa.ruruplan.plan.service.CustomPlanService;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import net.datasa.ruruplan.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

/**
 * GPT추천일정을 내 일정으로 담은 후 개별수정하는 컨트롤러
 * db에 이미 저장되어 있는 정보를 꺼내고, 다시 집어넣는 역할을 한다.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("myPage/myPlan")
public class CustomPlanController {

    private final CustomPlanService customPlanService;


    /**
     * 해당페이지 url 연결 메서드
     * 플랜번호만 하나 남기면 됨. (플랜 하나를 가져와서 출력하기 위한 메서드(html에 작성, origin용))
     * @param model
     * @return
     */
    @GetMapping("/{planNum}")
    public String customPlan(Model model, Locale locale, @PathVariable("planNum") Integer planNum) throws IOException {
        // custom.html로드될 때 뿌려줄 플랜정보불러오기
        PlanDTO planDTO = customPlanService.getPlan(planNum);
        log.debug("cmdNum: {}", planDTO.getCmdNum());
        log.debug("planDTO: {}", planDTO);

        // 기존에 선택한 옵션사항을 보여주기 위한 cmdDTO 불러오기
        GptCmdDTO cmdDTO = customPlanService.getCmd(planDTO.getCmdNum());
        
        // start_date, end_date 활용해서 몇박 며칠인지 정의하는 기능 ChronoUnit은 날짜타입의 계산을 도와주는 객체
        long days = ChronoUnit.DAYS.between(planDTO.getStartDate(), planDTO.getEndDate()) + 1;

        // 사용언어에 맞는 배열 설정
        String[] themeArray;
        if(locale.equals(Locale.KOREAN)) {
            themeArray = new String[] {"쇼핑", "식당", "카페", "역사", "문화", "힐링", "랜드마크", "체험", "레포츠", "아이"};
        } else {
            themeArray = new String[] {"ショッピング", "食べ物", "カフェ", "歴史", "文化", "ヒーリング", "ランドマーク", "体験", "レジャー", "子供"};
        }
        List<TaskDTO> taskList = planDTO.getTaskList();

        TaskDTO lastTaskDTO = planDTO.getTaskList().get(taskList.size()-1);
        Integer lastDay = lastTaskDTO.getDateN();
        model.addAttribute("days", days);
        model.addAttribute("planDTO", planDTO);
        model.addAttribute("themeArray", themeArray);
        model.addAttribute("lastDay", lastDay);
        model.addAttribute("cmdDTO", cmdDTO);
        log.debug("옵션정보:{}", cmdDTO);


        // 환율 값 불러오는 java 메소드 호출
        String value = exchangeTest.exchangeValue();
        double exchange = Double.parseDouble(value);
        double exchangeValue = Math.round(exchange*100) / 100.0 * 100;

        String exchangeValue2 = Double.toString(exchangeValue);

        if (exchangeValue2 == null) {
            exchangeValue2 = "0";
        }

        model.addAttribute("exchange", exchangeValue2);



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
        log.debug("taskDTO:{}", customPlanService.getTaskList(planNum, dayNum));
        return  customPlanService.getTaskList(planNum, dayNum);
    }

    /**
     * 테마별 / 테마 마커 출력시, 일자별 마커도 있어야 해서 같이 보냄
     * @param theme
     * @return
     */
    @ResponseBody
    @PostMapping("themeMarkers")
    public List<PlaceInfoDTO> themeMarker(@RequestParam("theme") String theme) {
        log.debug("입력받은 테마:{}", theme);
        return customPlanService.getThemeLocations(theme);
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
    public void updateDuration(@RequestParam("newDurationHour") String newDurationHour, @RequestParam("newDurationMinute") String newDurationMinute, @RequestParam("taskNum") Integer taskNum, @RequestParam("planNum") Integer planNum
    , @RequestParam("newCost") String newCost) {

        customPlanService.updateDuration(newDurationHour, newDurationMinute, taskNum, planNum, newCost);
    }

    @ResponseBody
    @PostMapping("placeInfoMore")
    public PlaceInfoDTO getPlaceInfo (@RequestParam("placeId") String placeId) {
        log.debug("장솢어보 : {}", customPlanService.getPlaceInfo(placeId));
        return customPlanService.getPlaceInfo(placeId);
    }

    @ResponseBody
    @PostMapping("updateTaskPlace")
    public void updateTaskPlace(@RequestParam("planNum") Integer planNum, @RequestParam("targetTaskNum") Integer targetTaskNum, @RequestParam("newPlaceId") String newPlaceId
                    ,@RequestParam("preTaskNum") Integer preTaskNum, @RequestParam("preTransDuration") double preTransDuration
                    ,@RequestParam("nextTaskNum") Integer nextTaskNum, @RequestParam("nextTransDuration") double nextTransDuration
                    ,@RequestParam("preTransType") String preTransType, @RequestParam("nextTransType") String nextTransType) {
    customPlanService.updateTaskPlace(planNum, targetTaskNum, newPlaceId, preTaskNum, preTransDuration, preTransType, nextTaskNum, nextTransDuration, nextTransType);
    }

    @ResponseBody
    @PostMapping("updateFirstTaskPlace")
    public void updateFirstTaskPlace(@RequestParam("planNum") Integer planNum, @RequestParam("targetTaskNum") Integer targetTaskNum, @RequestParam("newPlaceId") String newPlaceId
            ,@RequestParam("nextTaskNum") Integer nextTaskNum, @RequestParam("nextTransDuration") double nextTransDuration) {
        customPlanService.updateFirstTaskPlace(planNum, targetTaskNum, newPlaceId, nextTaskNum, nextTransDuration);
    }

    @ResponseBody
    @PostMapping("updateLastTaskPlace")
    public void updateLastTaskPlace(@RequestParam("planNum") Integer planNum, @RequestParam("targetTaskNum") Integer targetTaskNum, @RequestParam("newPlaceId") String newPlaceId
            ,@RequestParam("preTaskNum") Integer preTaskNum, @RequestParam("preTransDuration") double preTransDuration) {
        customPlanService.updateLastTaskPlace(planNum, targetTaskNum, newPlaceId, preTaskNum, preTransDuration);
    }

    @ResponseBody
    @PostMapping("addNewTask")
    public void addNewTask(@RequestParam("newPlaceId") String newPlaceId, @RequestParam("preTransDuration") double preTransDuration, @RequestParam("preTransType") String preTransType
    , @RequestParam("planNum") Integer planNum, @RequestParam("dayNum") Integer dayNum, @RequestParam("lastTaskNum") Integer lastTaskNum
    , @AuthenticationPrincipal AuthenticatedUser user) {
        customPlanService.addNewTask(newPlaceId, preTransDuration, preTransType, planNum, dayNum, lastTaskNum, user.getUsername());
    }

    @ResponseBody
    @PostMapping("deleteTask")
    public void deleteTask(@RequestParam("dayNum") Integer dayNum, @RequestParam("planNum") Integer planNum){
        customPlanService.deleteTask(dayNum, planNum);
    }

}
