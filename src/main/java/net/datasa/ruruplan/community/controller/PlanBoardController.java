package net.datasa.ruruplan.community.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.community.service.PlanBoardReplyService;
import net.datasa.ruruplan.community.service.PlanBoardService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("planBoard")
public class PlanBoardController {
    final PlanBoardService boardSer;
    final PlanBoardReplyService ReplySer;

    @GetMapping("list")
    public String planBoard() {
        return "communityView/board";
    }

    @Controller("share")
    public static class ShareController {

    }

    @Controller("save")
    public static class SaveController {

    }
}