package net.datasa.ruruplan.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

import org.springframework.web.servlet.LocaleResolver;

@Controller
@RequestMapping("locale")
public class LocaleController {

    private final LocaleResolver localeResolver;

    // 생성자에서 LocaleResolver 인터페이스로 주입
    public LocaleController(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    @GetMapping("/")
    public String home(@RequestParam(value = "lang", required = false) String lang,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Model model) {
        if (lang != null) {
            Locale locale = new Locale(lang);
            localeResolver.setLocale(request, response, locale); // 로케일을 변경
        }
        return "redirect:/";
    }
}
