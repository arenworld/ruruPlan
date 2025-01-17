package net.datasa.ruruplan.myPage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Slf4j
@Controller
public class UpdateInfoController {
    @GetMapping("updateInfoForm")
    public String updateInfoForm() {
        return "myPageView/updateInfoForm";
    }
}
