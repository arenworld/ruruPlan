package net.datasa.ruruplan.gpt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.gpt.domain.dto.GptCmdDTO;
//import net.datasa.ruruplan.gpt.service.GptResultService;
import net.datasa.ruruplan.gpt.domain.dto.GptResultDTO;
import net.datasa.ruruplan.gpt.service.GptResultService;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import net.datasa.ruruplan.plan.domain.dto.TaskDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GptResultController {

    private final GptResultService gptResultService;

    @ResponseBody
    @PostMapping("/getGptPlan")
    public GptResultDTO getGptPlan(@RequestParam("cmdNum") Integer cmdNum) throws IOException {
        log.debug("cmdNum: {}", cmdNum);
        return gptResultService.gptApi(cmdNum);
    }

    @GetMapping("/gptView/result")
    public String showGptResult(@RequestParam("cmdNum") Integer cmdNum, @RequestParam("placeIds") String placeIds, Model model) {
        List<String> placeIdList = Arrays.asList(placeIds.split(","));
        model.addAttribute("cmdNum", cmdNum);
        model.addAttribute("placeIdList", placeIdList);
        return "gptView/gptResult"; // gptResultView.html 파일로 이동
    }

    @GetMapping("gptView/question")
    public String gptViewQuestion() {
        return "gptView/question";
    }

    /**
     * gpt답변 커스터마이징하기 버튼 누르면 plan저장 후 customPlan 페이지 로드
     * @param planDTO
     * @param model
     * @return
     */
//    @PostMapping("customGptPlan")
//    public String customGptPlan(@ModelAttribute PlanDTO planDTO, Model model) {
//        gptResultService.saveGptPlan(planDTO);
//        Integer planNum = gptResultService.getPlanNum(planDTO.getPlanNum());
//        model.addAttribute("planNum", planNum);
//        return "customView/customPlan";
//    }
//
//    @GetMapping("reGptPlan")
//    public String reGptPlan(Model model) {
//
//    }
}

