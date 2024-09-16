package net.datasa.ruruplan.gpt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.gpt.domain.dto.GptCmdDTO;
//import net.datasa.ruruplan.gpt.service.GptResultService;
import net.datasa.ruruplan.gpt.service.GptResultService;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import net.datasa.ruruplan.plan.domain.dto.TaskDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GptResultController {

    private final GptResultService gptResultService;

    /**
     * ajax로 질문내용을 받아와서 gpt에게 질문 후 답변 내용 리턴(아직 plan테이블에 저장 x)
     * @param gptCmdDTO 사용자의 질문 내용
     * @return planDTO  gpt의 답변내용
     */
    @ResponseBody
    @PostMapping("getGptPlan")
    public List<String> getGptPlan(@RequestParam("cmdNum") Integer cmdNum) throws IOException {
        List<String> placeIdList = new ArrayList<>(gptResultService.gptApi(cmdNum));
        return placeIdList;
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

