package net.datasa.ruruplan.community.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.community.domain.dto.BoardDTO;
import net.datasa.ruruplan.community.domain.dto.ReplyDTO;
import net.datasa.ruruplan.community.service.BoardService;
import net.datasa.ruruplan.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 게시판 관련 컨트롤러
 */

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    @Value("${upload.path}")
    private String uploadPath;

    // 게시글 목록 조회
    @GetMapping("/list")
    public String getBoardList(Model model) {
        List<BoardDTO> boardList = boardService.getListAll();
        model.addAttribute("boardList", boardList);
        return "communityView/free_bulletin_board";
    }

    // 게시글 작성 폼 페이지로 이동
    @GetMapping("/write")
    public String writeForm() {
        return "communityView/writeForm";
    }

    // 게시글 작성 처리
    @PostMapping("/write")
    public String writeBoard(@ModelAttribute BoardDTO boardDTO,
                             @RequestParam(value = "upload", required = false) MultipartFile upload,
                             @AuthenticationPrincipal AuthenticatedUser user) {
        boardDTO.setMemberId(user.getUsername());
        boardService.write(boardDTO, uploadPath, upload);
        return "redirect:/board/list";
    }

    // 게시글 상세 조회
    @GetMapping("/read/{boardNum}")
    public String getBoard(@PathVariable int boardNum, Model model) {
        BoardDTO boardDTO = boardService.getBoard(boardNum);
        List<ReplyDTO> replyList = boardService.getReplyList(boardNum);
        log.debug("게시글 내용: {}", boardDTO.getContents());  // 게시글 내용 출력
        model.addAttribute("post", boardDTO);
        model.addAttribute("replyList", replyList);
        return "communityView/free_bulletin_board_concrete";
    }


    // 게시글 삭제
    @PostMapping("/delete/{boardNum}")
    public String delete(@PathVariable int boardNum,
                         @AuthenticationPrincipal AuthenticatedUser user) {
        boardService.delete(boardNum, user.getUsername(), uploadPath);
        return "redirect:/board/list";
    }

    // 게시글 수정 폼 페이지로 이동
    @GetMapping("/update/{boardNum}")
    public String updateForm(@PathVariable int boardNum, Model model) {
        BoardDTO boardDTO = boardService.getBoard(boardNum);
        model.addAttribute("board", boardDTO);
        return "communityView/updateForm";
    }

    // 게시글 수정 처리
    @PostMapping("/update/{boardNum}")
    public String update(@PathVariable int boardNum,
                         @ModelAttribute BoardDTO boardDTO,
                         @RequestParam(value = "upload", required = false) MultipartFile upload,
                         @AuthenticationPrincipal AuthenticatedUser user) {
        boardDTO.setBoardNum(boardNum);
        boardService.update(boardDTO, user.getUsername(), uploadPath, upload);
        return "redirect:/board/read/" + boardNum;
    }

    // 댓글 작성 처리
    @PostMapping("/replyWrite")
    public String replyWrite(@ModelAttribute ReplyDTO replyDTO,
                             @AuthenticationPrincipal AuthenticatedUser user) {
        replyDTO.setMemberId(user.getUsername());
        boardService.replyWrite(replyDTO);
        return "redirect:/board/read/" + replyDTO.getBoardNum();
    }


    // 댓글 삭제
    @PostMapping("/replyDelete/{replyNum}")
    public String replyDelete(@PathVariable int replyNum,
                              @RequestParam int boardNum,
                              @AuthenticationPrincipal AuthenticatedUser user) {
        boardService.replyDelete(replyNum, user.getUsername());
        return "redirect:/board/read/" + boardNum;
    }

    // 좋아요 기능
    @PostMapping("/like/{boardNum}")
    public String like(@PathVariable int boardNum) {
        boardService.like(boardNum);
        return "redirect:/board/read/" + boardNum;
    }
}
