package net.datasa.ruruplan.gpt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.gpt.domain.dto.GptCmdDTO;
import net.datasa.ruruplan.gpt.service.QuestionService;
import net.datasa.ruruplan.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("gptView")
@RequiredArgsConstructor
public class QuestionController {


    private final QuestionService questionService;


    /**
     * 질문 저장 및 질문내용(GptCmdDTO)반환해서 html로 전송, 아직 plan에 저장 안하고 그냥 답변 받아와서 보여 줄 것임.
     * @param gptCmdDTO 질문내용 저장
     * @param model cmdNum html로 전송
     * @param user  로그인 정보
     * @return  질문 결과창으로 이동
     */
    @PostMapping("saveGptCmd")
    public String saveGptCmd(GptCmdDTO gptCmdDTO, Model model,@AuthenticationPrincipal AuthenticatedUser user) {
        gptCmdDTO.setMemberId(user.getId());        // authenticatedUser 클래스의 id값
        GptCmdDTO dto = questionService.saveAndReturnDTO(gptCmdDTO);
        model.addAttribute("gptCmdDTO", dto); // cmdNum를 보내놓고 result.html에서 ajax post로 다시 보낼 것임.
        return "gptView/gptResult";
    }
}
