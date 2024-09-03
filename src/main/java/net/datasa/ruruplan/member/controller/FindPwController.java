package net.datasa.ruruplan.member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 비밀번호 찾기 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("login")
public class FindPwController {

    @GetMapping("findPw")
    public String findPw() {
        return "memberView/findPw";
    }
}
