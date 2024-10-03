package net.datasa.ruruplan.myPage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.myPage.service.MyPageService;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import net.datasa.ruruplan.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
public class MyPlanListController {

    private final MyPageService mypageSer;

    @GetMapping("myPlanList")
    public String myPlanList(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        String userId = user.getId();
        List<PlanDTO> MyPlanList = mypageSer.getMyPlanList(userId);

        model.addAttribute("userId", userId);
        model.addAttribute("MyPlanList", MyPlanList);

        return "myPageView/myPlanList";
    }
}
