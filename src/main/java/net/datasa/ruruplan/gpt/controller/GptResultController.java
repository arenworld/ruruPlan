package net.datasa.ruruplan.gpt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.gpt.domain.dto.PlanDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GptResultController {

//    private final GptResultService gptResultService

//    @ResponseBody
//    @GetMapping("getGptPlan")
//    public PlanDTO getGptPlan(@RequestParam("cmdNum") Integer cmdNum) {
//        PlanDTO planDTO = gptResultService.selectPlanDTO(cmdNum);
//        TaskDTO taskDTO = gptResultService.selectTaskDTO(planDTO.getPlanNum());
//    }
}

