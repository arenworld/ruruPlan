package net.datasa.ruruplan.gpt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.gpt.domain.dto.GptCmdDTO;
import net.datasa.ruruplan.gpt.service.QuestionService;
import net.datasa.ruruplan.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController // @Controller 대신 @RestController로 변경하여 JSON 응답
@RequestMapping("gptView")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("saveGptCmd")
    public ResponseEntity<GptCmdDTO> saveGptCmd(@RequestBody GptCmdDTO gptCmdDTO, @AuthenticationPrincipal AuthenticatedUser user) {
        gptCmdDTO.setMemberId(user.getId()); // 로그인된 사용자 ID 설정
        GptCmdDTO dto = questionService.saveAndReturnDTO(gptCmdDTO);

        // JSON 형식으로 반환
        return ResponseEntity.ok(dto);
    }
}
