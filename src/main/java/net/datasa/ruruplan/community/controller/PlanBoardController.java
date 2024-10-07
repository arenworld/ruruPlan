package net.datasa.ruruplan.community.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.community.domain.dto.PlanBoardDTO;
import net.datasa.ruruplan.community.service.BoardService;
import net.datasa.ruruplan.community.service.PlanBoardReplyService;
import net.datasa.ruruplan.community.service.PlanBoardService;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import net.datasa.ruruplan.security.AuthenticatedUser;
import net.datasa.ruruplan.security.AuthenticatedUserDetailService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("planBoard")
public class PlanBoardController {

    private final PlanBoardService boardSer;
    private final PlanBoardReplyService ReplySer;
    private final BoardService boardService;
    private final PlanBoardService planBoardService;

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
}