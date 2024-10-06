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

import java.util.List;

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
}