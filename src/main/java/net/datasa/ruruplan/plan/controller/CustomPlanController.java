package net.datasa.ruruplan.plan.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.plan.service.CustomPlanService;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("")
    public String customPlan(Model model) { //@RequestParam("planNum") Integer planNum) 지금은 테스트로 직접 넣고 나중에, planNum을 파람으로 받음
        PlanDTO planDTO = customPlanService.getPlan();
        model.addAttribute("planDTO", planDTO);
        log.debug("출력할 플래너값: {}", planDTO);
        return "customView/customPlan";
    }
}
