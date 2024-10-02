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


import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("planBoard")
public class PlanBoardController {

    final PlanBoardService boardSer;
    final PlanBoardReplyService ReplySer;

    //application.properties 파일의 게시판 관련 설정값
    @Value("${board.pageSize}")
    private int pageSize;

    @Value("${board.linkSize}")
    private int linkSize;

    @Value("${board.uploadPath}")
    private String uploadPath;

    @GetMapping("list")
    public String planBoard(Model model
            , @RequestParam(name = "page", defaultValue = "1") int page
            , @RequestParam(name = "searchType1", defaultValue = "") String searchType1
            , @RequestParam(name = "searchType2", defaultValue = "") String searchType2) {

        Page<PlanBoardDTO> boardPage = boardSer.getList(page, pageSize, searchType1, searchType2);

        model.addAttribute("boardPage", boardPage);

        return "communityView/board";
    }

    @Controller("share")
    public static class ShareController {

    }

    @Controller("save")
    public static class SaveController {

    }
}