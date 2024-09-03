package net.datasa.ruruplan.myPage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Slf4j
@Controller
public class MyPageController {

    @GetMapping("myPage")
    public String myPage() {
        return "myPageView/myPage";
    }
}
