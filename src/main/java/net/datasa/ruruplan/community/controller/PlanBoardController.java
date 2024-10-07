package net.datasa.ruruplan.community.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.community.domain.dto.PlanBoardDTO;
import net.datasa.ruruplan.community.service.BoardService;
import net.datasa.ruruplan.community.service.PlanBoardReplyService;
import net.datasa.ruruplan.community.service.PlanBoardService;
import net.datasa.ruruplan.exchangeTest;
import net.datasa.ruruplan.gpt.domain.dto.GptCmdDTO;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import net.datasa.ruruplan.plan.domain.dto.TaskDTO;
import net.datasa.ruruplan.plan.service.CustomPlanService;
import net.datasa.ruruplan.security.AuthenticatedUser;
import net.datasa.ruruplan.security.AuthenticatedUserDetailService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("planBoard")
public class PlanBoardController {

    private final PlanBoardService boardSer;
    private final PlanBoardReplyService ReplySer;
    private final BoardService boardService;
    private final PlanBoardService planBoardService;

    private final CustomPlanService customPlanService;

    //application.properties 파일의 게시판 관련 설정값
    @Value("${board.pageSize}")
    private int pageSize;

    @Value("${board.linkSize}")
    private int linkSize;

    @Value("${board.uploadPath}")
    private String uploadPath;

    @GetMapping("list")
    public String planBoard() {
        return "communityView/planBoard"; // 모델 데이터 없이 템플릿만 반환
    }

    @GetMapping("/listJson")
    @ResponseBody
    public Page<PlanBoardDTO> getPlanBoardListJson(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "tags", required = false) List<String> tags) {

        // tags 배열을 이용해 데이터 필터링
        return boardSer.getListByTags(page, pageSize, tags);
    }

    @PostMapping("savePlan")
    @ResponseBody
    public void savePlan(@RequestParam("boardNum") int boardNum, @AuthenticationPrincipal AuthenticatedUser user) {
        boardService.savePlan(boardNum, user.getId());
    }

    @PostMapping("likePlan")
    @ResponseBody
    public Integer likePlan(@RequestParam("boardNum") int boardNum, @AuthenticationPrincipal AuthenticatedUser user) {
        Integer res = boardSer.like(boardNum, user.getId());

        return res;
    }

    @PostMapping("/selectPlan")
    @ResponseBody
    public ResponseEntity<PlanDTO> selectPlan(@RequestParam("boardNum") Integer boardNum) {
        PlanDTO planDTO = planBoardService.selectPlan(boardNum);
        return ResponseEntity.ok(planDTO);
    }

    @GetMapping("/list/{planNum}/{boardNum}")
    public String getPlanBoard(Model model, Locale locale, @PathVariable("planNum") Integer planNum, @PathVariable("boardNum") Integer boardNum) throws IOException {

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
        model.addAttribute("boardNum", boardNum);
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

        return "customView/customPlanOrigin";
    };

}