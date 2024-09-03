package net.datasa.ruruplan.myPage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Slf4j
@Controller
public class MyPlanController {
    @GetMapping("myPlan")
    public String myPlan() {
        return "myPageView/myPlan";
    }
}
