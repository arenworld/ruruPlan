package net.datasa.ruruplan.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

@Slf4j
@RestController
public class LocaleController {

    private final LocaleResolver localeResolver;

    // 생성자에서 LocaleResolver 인터페이스로 주입
    public LocaleController(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    @GetMapping("locale")
    public void locale(@RequestParam(value = "lang", required = false) String lang,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Model model) {
        log.debug("locale: {}", lang);
        if (lang != null) {
            Locale locale = new Locale(lang);
            localeResolver.setLocale(request, response, locale); // 로케일을 변경

        }
    }

    @GetMapping("join/locale")
    public void joinlocale(@RequestParam(value = "lang", required = false) String lang,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Model model) {
        log.debug("locale: {}", lang);
        if (lang != null) {
            Locale locale = new Locale(lang);
            localeResolver.setLocale(request, response, locale); // 로케일을 변경

        }
    }

    @GetMapping("login/locale")
    public void loginlocale(@RequestParam(value = "lang", required = false) String lang,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Model model) {
        log.debug("locale: {}", lang);
        if (lang != null) {
            Locale locale = new Locale(lang);
            localeResolver.setLocale(request, response, locale); // 로케일을 변경

        }
    }

    @GetMapping("gptView/locale")
    public void gptcmdlocale(@RequestParam(value = "lang", required = false) String lang,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Model model) {
        log.debug("locale: {}", lang);
        if (lang != null) {
            Locale locale = new Locale(lang);
            localeResolver.setLocale(request, response, locale); // 로케일을 변경
        }
    }

    @GetMapping("custom/locale")
    public void customlocale(@RequestParam(value = "lang", required = false) String lang,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Model model) {
        log.debug("locale: {}", lang);
        if (lang != null) {
            Locale locale = new Locale(lang);
            localeResolver.setLocale(request, response, locale); // 로케일을 변경
        }
    }
}
