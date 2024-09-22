package net.datasa.ruruplan.gpt.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.gpt.domain.dto.GptCmdDTO;
import net.datasa.ruruplan.gpt.service.QuestionService;
import net.datasa.ruruplan.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("gptView")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * modal.js에서 ajax요청을 받아 사용자 선택지를 저장하고 cmdNum을 ajax로 반환
     * @param gptCmdDTO 사용자 선택을 DTO에 저장해서 DB로 보냄
     * @param user  현재 로그인된 사용자정보
     * @return  저장후 cmdNum을 가져와서 반환
     */
    @PostMapping("saveGptCmd")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveGptCmd(
            @RequestBody GptCmdDTO gptCmdDTO,
            @AuthenticationPrincipal AuthenticatedUser user,
            HttpSession session) {

        gptCmdDTO.setMemberId(user.getId()); // 로그인된 사용자 ID 설정
        Integer cmdNum = questionService.saveAndReturnDTO(gptCmdDTO);

        // 세션에 cmdNum 저장
        session.setAttribute("cmdNum", cmdNum);

        // 성공 메시지만 응답으로 전달
        Map<String, Object> response = new HashMap<>();
        response.put("message", "저장 완료");
        return ResponseEntity.ok(response);
    }

    /**
     * loading 페이지로 이동할 때 세션에서 cmdNum을 가져옴
     * @param session 세션 객체
     * @param model   loading 창으로 보낼 cmdNum 값 저장
     * @return cmdNum을 모델에 추가하여 반환
     */
    @GetMapping("loading")
    public String loading(HttpSession session, Model model) {
        Integer cmdNum = (Integer) session.getAttribute("cmdNum");
        if (cmdNum == null) {
            // cmdNum이 세션에 없을 경우 에러 처리
            return "error";
        }
        model.addAttribute("cmdNum", cmdNum);
        return "gptView/loading";
    }
}
