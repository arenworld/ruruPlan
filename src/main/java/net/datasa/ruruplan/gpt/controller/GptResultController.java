package net.datasa.ruruplan.gpt.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.gpt.domain.dto.GptCmdDTO;
//import net.datasa.ruruplan.gpt.service.GptResultService;
import net.datasa.ruruplan.gpt.domain.dto.GptResultDTO;
import net.datasa.ruruplan.gpt.service.GptResultService;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import net.datasa.ruruplan.plan.domain.dto.TaskDTO;
import net.datasa.ruruplan.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GptResultController {

    private final GptResultService gptResultService;


    /**
     * 로딩창 ajax에서 요청을 받아 planDTO를 만드는 메서드
     * @param cmdNum    로딩창에서 ajax로 받아온 값
     * @param session   세션에 planDTO를 저장할 예정
     * @return  리다이렉트 및 페이지 이동 요청
     * @throws IOException  책임회피
     */
    @PostMapping("/getGptPlan")
    public String getGptPlan(@RequestParam("cmdNum") Integer cmdNum, HttpSession session) throws IOException {
        log.debug("cmdNum: {}", cmdNum);

        // GPT API 호출 및 PlanDTO 생성
        GptResultDTO result = gptResultService.gptApi(cmdNum);
        PlanDTO plan = gptResultService.planCreate(result);

        // PlanDTO를 세션에 저장
        session.setAttribute("planDTO", plan);

        // 여기서 리다이렉트는 브라우저에게 /gptView/result로 요청을 보내라고 알려주는 것
        return "redirect:/gptView/result";
    }

    /**
     * 세션값에 저장된 PlanDTO를 모델에 넣어 gptResult.html로 보내는 메서드
     * @param session   저장된 PlanDTO불러오기 위함
     * @param model     planDTO를 모델값에 저장
     * @return          결과창으로 페이지 이동
     */
    @GetMapping("/gptView/result")
    public String getGptResultPage(HttpSession session, Model model) {
        // 세션에서 PlanDTO를 가져옴
        PlanDTO planDTO = (PlanDTO) session.getAttribute("planDTO");
        Integer cmdNum = planDTO.getCmdNum();
        GptCmdDTO gptCmdDTO = gptResultService.findGptCmdDTO(cmdNum);

        model.addAttribute("cmdDTO", gptCmdDTO);

        // theme1, theme2, theme3을 배열로 묶기
        List<String> themeArray = Arrays.asList(planDTO.getTheme1(), planDTO.getTheme2(), planDTO.getTheme3());

        // 모델에 추가
        model.addAttribute("themeArray", themeArray);


        LocalDate startDate = planDTO.getStartDate();
        LocalDate endDate = planDTO.getEndDate();
        long lastDay = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        model.addAttribute("lastDay", lastDay);

        // 일자별로 TaskDTO 리스트를 분류하기 위한 Map
        Map<Integer, List<TaskDTO>> taskByDateMap = new HashMap<>();

        // planDTO 안의 taskList를 반복하면서 dateN에 따라 리스트를 분류
        for (TaskDTO task : planDTO.getTaskList()) {
            Integer dateN = task.getDateN();

            // 해당 dateN에 해당하는 리스트가 없으면 새로 생성
            taskByDateMap.putIfAbsent(dateN, new ArrayList<>());

            // 해당 날짜의 리스트에 task 추가
            taskByDateMap.get(dateN).add(task);
        }

        // 일자별 taskList를 모델에 추가
        model.addAttribute("taskByDateMap", taskByDateMap);

        // 모델에 PlanDTO를 추가하여 화면에서 사용할 수 있게 함
        model.addAttribute("planDTO", planDTO);

        // gptResult.html 페이지로 이동
        return "gptView/gptResult";
    }

    // 임시로 만든 것이라 나중에 삭제하고 다른 곳으로 옮길 예정.
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


    @PostMapping("/saveGptPlan")
    @ResponseBody
    public void saveGptPlan(@RequestBody PlanDTO planDTO, @AuthenticationPrincipal AuthenticatedUser user) {     // Json 데이터 받을때는 RequestBody 사용해야함
        gptResultService.saveGptPlan(planDTO, user.getId());
    }
}

