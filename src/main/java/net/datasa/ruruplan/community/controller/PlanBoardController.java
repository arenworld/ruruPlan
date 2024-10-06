package net.datasa.ruruplan.community.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.community.domain.dto.PlanBoardDTO;
import net.datasa.ruruplan.community.service.PlanBoardReplyService;
import net.datasa.ruruplan.community.service.PlanBoardService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("planBoard")
public class PlanBoardController {

    private final PlanBoardService boardSer;
    private final PlanBoardReplyService ReplySer;

    //application.properties 파일의 게시판 관련 설정값
    @Value("${board.pageSize}")
    private int pageSize;

    @Value("${board.linkSize}")
    private int linkSize;

    @Value("${board.uploadPath}")
    private String uploadPath;

    @GetMapping("list")
    public String planBoard(Model model,
                            @RequestParam(name = "page", defaultValue = "1") int page,
                            @RequestParam(name = "tag1", defaultValue = "") String tag1,
                            @RequestParam(name = "tag2", defaultValue = "") String tag2,
                            @RequestParam(name = "tag3", defaultValue = "") String tag3,
                            @RequestParam(name = "tag4", defaultValue = "") String tag4,
                            @RequestParam(name = "tag5", defaultValue = "") String tag5,
                            @RequestParam(name = "tag6", defaultValue = "") String tag6) {

        // Fetch board data with pagination
        Page<PlanBoardDTO> boardPage = boardSer.getList(page, pageSize, tag1, tag2);

        // Add necessary attributes to the model
        model.addAttribute("boardPage", boardPage);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("linkSize", linkSize);
        model.addAttribute("tag1", tag1);
        model.addAttribute("tag2", tag2);
        model.addAttribute("tag3", tag3);
        model.addAttribute("tag4", tag4);
        model.addAttribute("tag5", tag5);
        model.addAttribute("tag6", tag6);

        return "communityView/planBoard"; // This should point to the Thymeleaf HTML file
    }

    @GetMapping("/listJson")
    @ResponseBody
    public Page<PlanBoardDTO> getPlanBoardListJson(@RequestParam(name = "page", defaultValue = "1") int page,
                                                   @RequestParam(name = "tag1", defaultValue = "") String tag1,
                                                   @RequestParam(name = "tag2", defaultValue = "") String tag2,
                                                   @RequestParam(name = "tag3", defaultValue = "") String tag3,
                                                   @RequestParam(name = "tag4", defaultValue = "") String tag4,
                                                   @RequestParam(name = "tag5", defaultValue = "") String tag5,
                                                   @RequestParam(name = "tag6", defaultValue = "") String tag6) {

        // Page로 받은 데이터 그대로 반환 (JSON 형태로 반환됨)
        return boardSer.getList(page, pageSize, tag1, tag2);
    }
}