package net.datasa.ruruplan.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.member.repository.MemberRepository;
import net.datasa.ruruplan.member.service.JoinService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 회원가입 컨트롤러
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("join")
public class JoinController {

    private final JoinService joinService;

    /**
     * 회원가입 폼으로 이동
     * @return
     */
    @GetMapping("joinForm")
    public String joinForm() {
        return "memberView/joinForm";
    }

    /**
     * id 중복확인
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("idDuplicate")
    public boolean idDuplicate(@RequestParam("id") String id) {

        boolean res = joinService.idDuplicate(id);

        return res;
    }
}
