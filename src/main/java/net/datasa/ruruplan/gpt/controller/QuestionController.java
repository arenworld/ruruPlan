package net.datasa.ruruplan.gpt.controller;

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
    public ResponseEntity<Map<String, Object>> saveGptCmd(@RequestBody GptCmdDTO gptCmdDTO, @AuthenticationPrincipal AuthenticatedUser user) {
        gptCmdDTO.setMemberId(user.getId()); // 로그인된 사용자 ID 설정
        Integer cmdNum = questionService.saveAndReturnDTO(gptCmdDTO);

        // cmdNum을 JSON 형태로 반환
        Map<String, Object> response = new HashMap<>();
        response.put("cmdNum", cmdNum);

        return ResponseEntity.ok(response);
    }

    /**
     * modal.js에서 ajax실행 성공시 loading으로 이동(url값에 cmdNum포함)
     * @param cmdNum    modal.js에서 보낸 url값에 있는 cmdNum
     * @param model     loading창으로 보낼 cmdNum값 저장
     * @return  cmdNum
     */
    @GetMapping("loading")
    public String loading(@RequestParam("cmdNum") Integer cmdNum, Model model) {
        model.addAttribute("cmdNum", cmdNum);
        return "gptView/loading";
    }
}
