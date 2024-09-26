package net.datasa.ruruplan.community.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.community.service.PlanBoardReplyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("planBoard")
public class PlanBoardReplyController {
    final PlanBoardReplyService ReplySer;
}
