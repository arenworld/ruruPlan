package net.datasa.ruruplan.plan.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * 플랜 하나를 가져와서 출력하기 위한 메서드
     * @param model
     * @return
     */
    @GetMapping("")
    public String customPlan(Model model) { //@RequestParam("planNum") Integer planNum) 지금은 테스트로 직접 넣고 나중에, planNum을 파람으로 받음

        // custom.html로드될 때 뿌려줄 플랜정보불러오기
        PlanDTO planDTO = customPlanService.getPlan();

        // start_date, end_date 활용해서 몇박 며칠인지 정의하는 기능 ChronoUnit은 날짜타입의 계산을 도와주는 객체
        long days = ChronoUnit.DAYS.between(planDTO.getStartDate(), planDTO.getEndDate()) + 1;

        model.addAttribute("days", days);
        model.addAttribute("planDTO", planDTO);
        // log.debug("저장한 위치값: {}", locationsAll);
        // log.debug("출력할 플래너 정보: {}", planDTO);
        return "customView/customPlan";
    };

    @ResponseBody
    @PostMapping("mapLoading")
    public List<Map<String, Double>> mapLoading() {
        return customPlanService.getLocationsAll();
    };

    @ResponseBody
    @PostMapping("allPlanMarker")
    public List<Map<String, Double>> allPlanMarker(@RequestParam ("planNum") Integer planNum) {
        return customPlanService.getPlanLocations(planNum);
    }

    @ResponseBody
    @PostMapping("dayPlanMarker")
    public List<Map<String, Double>> dayPlanMarker(@RequestParam ("planNum") Integer planNum, @RequestParam("dateNum") Integer dateNum) {
        return customPlanService.getDayLocations(planNum, dateNum);
    }

    /**
     * 일자별 일정 리스트 가져오기
     * @param model
     * @param planNum
     */
    @GetMapping("test")
    public void test(Model model, @RequestParam("planNum") Integer planNum, @RequestParam("dateN") Integer dateN) {
        customPlanService.getDayTaskList(planNum, dateN);
    }
}
