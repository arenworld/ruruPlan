package net.datasa.ruruplan.member.controller;

import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.member.Route;
import net.datasa.ruruplan.plan.domain.dto.TaskDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class test {

    @GetMapping("RtestResult")
    public String test() {

        Route route = new Route();
        TaskDTO dto = route.PublicRoute();

        log.debug(dto.toString());
        return "redirect:/";
    }
}
