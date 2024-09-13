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

    @PostMapping("route")
    @ResponseBody
    public StringBuilder route(@RequestParam("sx") String sx, @RequestParam("sy") String sy,
                               @RequestParam("ex") String ex, @RequestParam("ey") String ey) {
        try {
            log.debug("여기 까지 왔음");
            log.debug("받은 검색자료: {},{},{},{}", sx, sy, ex, ey);
            RestTemplate restTemplate = new RestTemplate();
            String apiURL = "https://api.odsay.com/v1/api/searchPubTransPathT?SX=" + sx + "&SY="
                    + sy + "&EX=" + ex + "&EY=" + ey + "&apiKey=" + URLEncoder.encode("AbfWDSywAKWcKBRv/FClpFQ", "UTF-8");

            String data = restTemplate.getForObject(apiURL, String.class);  // API 호출 및 결과 받기
            log.debug("API도 잘 실행 되었음: {}", apiURL);
            
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(data);  // JSON 파싱
            log.debug("파싱도 잘 실행 되었음11: {}", root.toString());
            JsonNode pathList = root.path("result").path("path");
            log.debug("파싱도 잘 실행 되었음22: {}", pathList.toString());

            // HTML로 출력할 데이터 저장
            StringBuilder htmlContent = new StringBuilder();
            for (JsonNode path : pathList) {
                int totalTime = path.path("info").path("totalTime").asInt();
                htmlContent.append("<p>총 소요 시간: " + totalTime + "분</p>");
            }
            log.debug("자료만들었음: {}", htmlContent);
            return htmlContent;
        
        } catch (Exception e) {
            log.error("여기서 문제생김");
            e.printStackTrace();
        }
        return null;
    }

}
