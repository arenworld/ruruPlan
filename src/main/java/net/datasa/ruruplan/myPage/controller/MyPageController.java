package net.datasa.ruruplan.myPage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.community.domain.dto.PlanBoardDTO;
import net.datasa.ruruplan.community.domain.dto.SavePlanDTO;
import net.datasa.ruruplan.community.service.PlanBoardService;
import net.datasa.ruruplan.community.service.SavePlanService;
import net.datasa.ruruplan.member.domain.dto.MemberDTO;
import net.datasa.ruruplan.member.service.JoinService;
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
    final PlanBoardService planBoardService;    // 공유 게시판
    final SavePlanService savePlanService;  //북마크 서비스
    final JoinService joinService;

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

    /**
     * 내 플랜 목록
     * @param user
     * @param model
     * @return
     */
    @GetMapping("myPlanList")
    public String myPlanList(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        String userId = user.getId();
        List<PlanDTO> myPlanList = myPageService.getMyPlanList(userId);

        MemberDTO member = myPageService.getMemberInfo(userId);
        model.addAttribute("member", member);

        model.addAttribute("myPlanList", myPlanList);

        return "myPageView/myPlanList";
    }

    /**
     * 플랜 상세 페이지
     * @return
     */
    @GetMapping("myPlan")
    public String myPlan() {
        return "myPageView/myPlan";
    }

    /**
     * 나의 플랜 공유 플랜 게시판에 게시(저장)
     * @param planNum
     */
    @ResponseBody
    @PostMapping("planShare")
    public void myPlanShare(@RequestParam("planNum") Integer planNum, @AuthenticationPrincipal AuthenticatedUser user) {

        String userId = user.getId();

        planBoardService.sharePlan(planNum, userId);
    }

    /**
     * 북마크한 플랜 목록
     * @param user
     * @param model
     * @return
     */
    @GetMapping("bookmarks")
    public String bookmarks(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        String userId = user.getId();
        List<SavePlanDTO> bookmarks = savePlanService.getbookmarks(userId);

        MemberDTO member = myPageService.getMemberInfo(userId);
        model.addAttribute("member", member);

        log.debug("이거 받아옴 {}",bookmarks.toString());
        model.addAttribute("bookmarks", bookmarks);

        return "myPageView/bookmarks";
    }


    /**
     * 내정보 열람 및 수정
     * @param user
     * @param model
     * @return
     */
    @GetMapping("myinfo")
    public String myinfo(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        
        String userId = user.getId();
        MemberDTO member = myPageService.getMemberInfo(userId);
        model.addAttribute("member", member);

        return "myPageView/myinfo";
    }

    @PostMapping("updateinfo")
    public String update(@ModelAttribute MemberDTO member) {

       joinService.update(member);

        return "redirect:/myPage";
    }
}
