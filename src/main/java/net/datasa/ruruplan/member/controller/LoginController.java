package net.datasa.ruruplan.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.member.service.FindIdService;
import net.datasa.ruruplan.member.service.FindPwService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 로그인 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("login")
@RequiredArgsConstructor
public class LoginController {

    final private FindIdService findIdService;
    final private FindPwService findPwService;

    /**
     * 로그인 폼으로 이동
     * @return 로그인.html
     */
    @GetMapping("/loginForm")
    public String loginForm() {
        return "memberView/loginForm";
    }

    /**
     * 아이디 찾기 페이지로 이동
     * @return 아이디 찾기.html
     */
    @GetMapping("findId")
    public String findId() {
        return "memberView/findId";
    }

    /**
     * 아이디 찾기 결과
     * @param email 입력받은 이메일
     * @param model 검색 결과
     * @return 아이디 찾기 결과.html
     */
    @PostMapping("findId")
    public String findId(@RequestParam ("email") String email, Model model) {

        log.debug("여기까지 왔음{}", email);
        String result = findIdService.findId(email);    // 아이디를 못 찾으면 입력받은 email을 재반환

        // 아이디를 찾으면 앞 4자리 제외 *처리
            if(!result.contains("@")) {
                String visiblePart = result.substring(0, 4); // 앞 4자리
                String maskedPart = "*".repeat(result.length() - 4); // 나머지 부분 * 처리
                result = visiblePart + maskedPart;
            }

        model.addAttribute("result", result);

        return "memberView/findIdResult";
    }

    /**
     * 비밀번호 찾기 페이지로 이동
     * @return 비밀번호 찾기.html
     */
    @GetMapping("findPw")
    public String findPw() {
        return "memberView/findPw";
    }

    /**
     * 아이디 존재여부 확인 및 이메일 반환
     * @param id 입력받은 ID
     * @return 입력받은 ID에 해당하는 email
     */
    @ResponseBody
    @PostMapping("idConfirm")
    public String idConfirm(@RequestParam ("id") String id) {

        String res = findPwService.findPw(id);

        log.debug("입력받은 ID에 해당하는 email: {}",res);
        return res;
    }

    /**
     *
     * @param memberId
     * @param model
     * @return
     */
    @PostMapping("findPw")
    public String resetPwForm(@RequestParam("memberId") String memberId, Model model) {

        model.addAttribute("memberId", memberId);
        return "memberView/resetPw";
    }

    @PostMapping("resetPw")
    public String resetPw(@RequestParam("memberId") String memberId,
                          @RequestParam("memberPw") String memberPw, Model model) {

        log.debug("전달받은 아이디, 새 패스워드: {}, {}", memberId, memberPw);
        findPwService.resetPw(memberId, memberPw);

        return "redirect:/login/loginForm";
    }

}
