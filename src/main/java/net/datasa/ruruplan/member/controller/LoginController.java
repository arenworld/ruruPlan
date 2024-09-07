package net.datasa.ruruplan.member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 로그인 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("login")
public class LoginController {

    @GetMapping("/loginForm")
    public String loginForm() {
        return "memberView/loginForm";
    }

}
