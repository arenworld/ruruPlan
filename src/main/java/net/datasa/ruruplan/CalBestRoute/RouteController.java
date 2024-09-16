package net.datasa.ruruplan.CalBestRoute;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;

@Slf4j
@Controller
public class RouteController {

    @GetMapping("test")
    public String test() {
        return "memberView/routeTest";
    }

}
