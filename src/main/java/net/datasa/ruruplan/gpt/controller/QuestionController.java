package net.datasa.ruruplan.gpt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.gpt.domain.dto.GptCmdDTO;
import net.datasa.ruruplan.gpt.service.QuestionService;
import net.datasa.ruruplan.security.AuthenticatedUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("gpt")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    /**
     * 추천받기 버튼 누르면 질문페이지를 리턴
     * @return  질문 페이지
     */
    @GetMapping("question")
    public String question() {
        return "gptView/question";
    }


    /**
     * 질문 저장 및 cmdNum반환해서 html로 전송
     * @param gptCmdDTO 질문내용 저장
     * @param model cmdNum html로 전송
     * @param user  로그인 정보
     * @return  질문 결과창으로 이동
     */
//    @GetMapping("saveGptCmd")
//    public String saveGptCmd(GptCmdDTO gptCmdDTO, Model model, AuthenticatedUser user) {
//        gptCmdDTO.setMemberId(user.id);     // authenticatedUser 클래스의 id값
//        Integer cmdNum = questionService.saveAndReturnId(gptCmdDTO);
//        model.addAttribute("cmdNum", cmdNum); // cmdNum를 보내놓고 result.html에서 ajax post로 다시 보낼 것임.
//        return "gptView/gptResult";
//    }
}
