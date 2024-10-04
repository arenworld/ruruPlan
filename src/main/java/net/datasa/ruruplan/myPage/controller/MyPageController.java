package net.datasa.ruruplan.myPage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.community.domain.dto.PlanBoardDTO;
import net.datasa.ruruplan.community.service.PlanBoardService;
import net.datasa.ruruplan.member.domain.dto.MemberDTO;
import net.datasa.ruruplan.myPage.service.MyPageService;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import net.datasa.ruruplan.security.AuthenticatedUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.util.WebUtils;
import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor  // 생성자 주입을 자동으로 생성
@Slf4j  // 로깅 기능을 사용
@Controller  // 이 클래스를 Spring 컨트롤러로 지정
@RequestMapping("/myPage")
public class MyPageController {

    final MyPageService myPageService;  // 서비스 의존성 주입
    final PlanBoardService planBoardService;

    @GetMapping("")
    public String myPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        String username = userDetails.getUsername();  // 인증된 사용자의 username 가져오기
        MemberDTO member = myPageService.getMemberInfo(username);
        model.addAttribute("member", member);

        // CSRF 토큰은 Thymeleaf에서 자동으로 처리되므로 명시적으로 가져오지 않아도 됩니다.

        return "myPageView/myPage";  // Thymeleaf 템플릿으로 렌더링
    }

    @GetMapping("myPlanList")
    public String myPlanList(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        String userId = user.getId();
        List<PlanDTO> myPlanList = myPageService.getMyPlanList(userId);

        model.addAttribute("userId", userId);
        model.addAttribute("myPlanList", myPlanList);

        return "myPageView/myPlanList";
    }

    @GetMapping("myPlan")
    public String myPlan() {
        return "myPageView/myPlan";
    }

    @GetMapping("myPlanShare")
    public String myPlanShare(Model model, @RequestParam("planNum") int planNum) {

        PlanDTO dto = myPageService.getPlanShare(planNum);

        model.addAttribute("dto", dto);

        return "myPageView/myPlanShare";
    }

    @PostMapping("myPlanShare")
    public String myPlanShare(@ModelAttribute PlanBoardDTO dto) {

        planBoardService.save(dto);

        return "redirect:/";
    }

}
