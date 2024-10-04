package net.datasa.ruruplan;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
    public class HomeController {

        @GetMapping({"", "/"})
        public String home(Model model) throws IOException {

            // 환율 값 불러오는 java 메소드 호출
            double exchangeValue = exchangeTest.exchangeValue();
            double exchangeValue2 = Math.round(exchangeValue * 100) / 100.0;

            exchangeValue2 *= 100;

            String exchange = exchangeValue2 + "₩/100￥";

            model.addAttribute("exchange", exchange);

            return "home";
        }
    }


