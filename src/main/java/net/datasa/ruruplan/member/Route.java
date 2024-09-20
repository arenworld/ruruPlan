package net.datasa.ruruplan.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.plan.domain.dto.TaskDTO;
import org.apache.tomcat.util.json.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;

@Slf4j
public class Route {

    // API 키 설정
    String apiKey = "I0LAbZwqx1u0tlR35lXfH7985F6KYCCfUXI9jIFzeS0";
    String sx = "127.0979006";
    String sy = "37.51135169";
    String ex = "126.9865899";
    String ey = "37.56196326";

    public TaskDTO PublicRoute(/*String sx, String sy, String ex, String ey*/) {

        log.debug("좌표: {},{},{},{}", sx, sy, ex, ey);

        try {
            /*// API URL 생성
            String urlString = "https://api.odsay.com/v1/api/searchPubTransPathT?SX=" + sx + "&SY=" + sy +
                    "&EX=" + ex + "&EY=" + ey + "&apiKey=" + apiKey;

            URL url = new URL(urlString);

            log.debug("url: {}", url);

            // HTTP GET 요청 설정
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 응답 받기
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // JSON 응답 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.toString());
            log.debug("제이슨: {}", jsonResponse);
            // path 배열 찾기
            JsonNode pathArray = jsonResponse.path("result").path("path");

            // 가장 빠른 경로 찾기 (기본적으로 첫 번째 경로로 설정)
            JsonNode fastestRoute = pathArray.get(0);
            for (int i = 1; i < pathArray.size(); i++) {
                JsonNode currentRoute = pathArray.get(i);
                if (currentRoute.path("info").path("totalTime").asInt() <
                        fastestRoute.path("info").path("totalTime").asInt()) {
                    fastestRoute = currentRoute;
                }
            }
            JsonNode routeInfo = fastestRoute.path("info");
            TaskDTO taskDTO = TaskDTO.builder().
                    duration(LocalTime.of(routeInfo.path("totalTime").asInt() / 60,
                            routeInfo.path("totalTime").asInt() % 60)).
                    cost(routeInfo.path("payment").asInt()).
                    task("대중교통").
                    build();

            // 필요한 정보 추출

            System.out.println("총 시간: " + routeInfo.path("totalTime").asInt());
            System.out.println("금액: " + routeInfo.path("payment").asInt());
            System.out.println("출발역: " + routeInfo.path("firstStartStation").asText());
            System.out.println("도착역: " + routeInfo.path("lastEndStation").asText());

            // 필요한 DB 저장 로직 추가
            // saveToDatabase(routeInfo);
        return taskDTO;*/

            // 1. Tmap API 호출
            String apiUrl = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&format=json";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("appKey", "7ejrjQSxsM8Vp5U8WbLArOuHpOwQNnJ31hqE3Pt7");
            conn.setDoOutput(true);

            // 요청 데이터
            String jsonInputString = "{"
                    + "\"startX\":\"127.0979006\","
                    + "\"startY\":\"37.51135169\","
                    + "\"endX\":\"126.9865899\","
                    + "\"endY\":\"37.56196326\","
                    + "\"reqCoordType\":\"WGS84GEO\","
                    + "\"resCoordType\":\"EPSG3857\","
                    + "\"startName\":\"출발지\","
                    + "\"endName\":\"도착지\""
                    + "}";

            // 요청 보내기
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 응답 받기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.toString());
            log.debug("제이슨: {}", jsonResponse);

            JsonNode pathArray = jsonResponse.path("resultData").path("properties");

            log.debug(pathArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
