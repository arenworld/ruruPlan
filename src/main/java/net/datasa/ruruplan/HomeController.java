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
            String value = exchangeTest.exchangeValue();
            double exchange = Double.parseDouble(value);
            double exchangeValue = Math.round(exchange*100) / 100.0 * 100;

            String exchangeValue2 = Double.toString(exchangeValue);
            String text = exchangeValue2 + "₩/100￥";

            model.addAttribute("exchange", text);

            return "home";
        }
    }


