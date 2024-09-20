package net.datasa.ruruplan.gpt.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.gpt.domain.dto.GptResultDTO;
import net.datasa.ruruplan.gpt.domain.entity.GptCmdEntity;
import net.datasa.ruruplan.gpt.repository.GptCmdRepository;
import net.datasa.ruruplan.plan.repository.PlaceInfoRepository;
import net.datasa.ruruplan.plan.repository.PlanRepository;
import net.datasa.ruruplan.plan.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GptResultService {

    private final GptCmdRepository gptCmdRepository;
    private final PlanRepository planRepository;
    private final TaskRepository taskRepository;
    private final PlaceInfoRepository placeInfoRepository;

    public GptResultDTO gptApi(Integer cmdNum) throws IOException {

        GptCmdEntity gptCmdEntity = gptCmdRepository.findById(cmdNum).orElseThrow(() -> new EntityNotFoundException("cmdNum으로 Entity를 불러오지 못했습니다: " + cmdNum));

        // GptCmdDTO에서 세부 정보를 추출
        LocalDate firstDate = gptCmdEntity.getFirstDate();
        LocalDate lastDate = gptCmdEntity.getLastDate();
        LocalTime arrival = gptCmdEntity.getArrival();
        float floatArrival = 0;
        float floatDepart = 0;
        if (arrival != null) floatArrival = arrival.getHour() + arrival.getMinute() / 60.0f;   // 항공편에 따른 일정 개수 계산을 위함

        LocalTime depart = gptCmdEntity.getDepart();
        if (depart != null) floatDepart = depart.getHour() + depart.getMinute() / 60.0f;  // 항공편에 따른 일정 개수 계산을 위함

        String theme1 = gptCmdEntity.getTheme1();
        String theme2 = gptCmdEntity.getTheme2();
        String theme3 = gptCmdEntity.getTheme3();
        boolean density = gptCmdEntity.getDensity();

        log.debug("firstDate: " + firstDate);
        log.debug("lastDate: " + lastDate);
        log.debug("arrival: " + arrival);
        log.debug("depart: " + depart);
        log.debug("theme1: " + theme1);
        log.debug("theme2: " + theme2);
        log.debug("theme3: " + theme3);
        log.debug("density: " + density);


        // 반환할 placeId 리스트 생성
        List<String> placeIdList = new ArrayList<>();

        String argument;
        // GPT에게 전달할 argument 생성
        if(arrival != null && depart != null) {
            float totalFirstDayTime;
            float totalLastDayTime;

            if (density) { // 빽빽한 일정일 경우
                totalFirstDayTime = 22 - floatArrival - 3;
                totalLastDayTime = floatDepart - 10 - 3;
            } else { // 널널한 일정일 경우
                totalFirstDayTime = 21 - floatArrival - 3;
                totalLastDayTime = floatDepart - 10 - 3;
            }
            // GPT에게 질문할 문구 생성
            argument = gptQuestion2(density, firstDate, lastDate, theme1, theme2, theme3, totalFirstDayTime, totalLastDayTime);
        } else {
            // GPT에게 질문할 문구 생성
            argument = gptQuestion(density, firstDate, lastDate, theme1, theme2, theme3);
        }
        log.debug("질문내용: " + argument);


        // 파이썬 스크립트 경로 설정
        String firstPythonScriptPath = "src/test/python/newQuestion.py";
        String pythonInterpreter = "python";

        try {
            // 첫 번째 파이썬 스크립트 실행
            ProcessBuilder processBuilder = new ProcessBuilder(pythonInterpreter, firstPythonScriptPath, argument);
            Process process = processBuilder.start();
            // 파이썬에서 출력된 값 읽어오는 객체 생성
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;



            // GPT 출력 값을 한 줄씩 읽기
            while ((line = reader.readLine()) != null) {
                log.debug("답변내용 :" + line);
                // '-'를 기준으로 나눠서 활동 유형과 장소 정보로 분리
                if (line.contains("-") && line.contains(":")) {
                    String[] typeAndPlace = line.split("-", 2); // "관광지-장소명:구" 형식에서 분리
                    if (typeAndPlace.length == 2) {
                        String placeType = typeAndPlace[0].trim(); // "관광지", "식당", "카페" 중 하나
                        String placeInfo = typeAndPlace[1].trim(); // "장소명:구"
                        String[] parts = placeInfo.split(":", 2); // ':'을 기준으로 장소명과 구 분리
                        if (parts.length == 2) {
                            String name = parts[0].trim();
                            String address = parts[1].trim();
                            log.debug("name: " + name);
                            log.debug("address: " + address);

                            // DB 일치 여부 확인 및 placeId 반환
                            String placeId = checkSimilarity(name, address);
                            log.debug("placeId: " + placeId);

                            if (placeId.equals("일치하는 정보 없음")) {
                                // '식당'이나 '카페'라면 해당 테마를 가진 장소를 랜덤으로 선택
                                if (placeType.equals("식당") || placeType.equals("카페")) {
                                    placeId = changeFoodOrCafe(address, placeType, placeIdList);
                                } else {
                                    // 그 외의 경우, 그룹화해서 랜덤 선택
                                    placeId = changeTour(address, theme1, theme2, theme3, placeIdList);
                                }
                                log.debug("랜덤으로 선택된 placeId: " + placeId);
                            }

                            // placeId를 리스트에 추가
                            placeIdList.add(placeId);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("IOException 발생", e);
        }
        log.debug("place_id: {}", placeIdList);

        GptResultDTO dto = new GptResultDTO();
        dto.setCmdNum(cmdNum);
        dto.setPlaceIdList(placeIdList);
        return dto; // 변경된 반환 값 (placeId 리스트 반환)
    }


    // 여행 테마와 일정 기반으로 GPT에게 전달할 '질문' 구조 만들기
    private String gptQuestion(boolean density, LocalDate firstDate, LocalDate lastDate, String theme1, String theme2, String theme3) {
        int days = firstDate.until(lastDate).getDays() + 1;
        String season = Season(firstDate.getMonthValue());

        // 첫날 활동 리스트
        List<String> firstDayActivities = new ArrayList<>(List.of(
                "식당-식당명:구",
                "관광지-관광지명:구",
                "카페-카페명:구",
                "관광지-관광지명:구",
                "식당-식당명:구",
                "관광지-관광지명:구"
        ));

        // 중간 날짜들 활동 리스트
        List<String> middleDayActivities = new ArrayList<>(List.of(
                "관광지-관광지명:구",
                "식당-식당명:구",
                "관광지-관광지명:구",
                "카페-카페명:구",
                "관광지-관광지명:구",
                "식당-식당명:구",
                "관광지-관광지명:구"
        ));

        // 마지막 날 활동 리스트
        List<String> lastDayActivities = new ArrayList<>(List.of(
                "관광지-관광지명:구",
                "식당-식당명:구",
                "관광지-관광지명:구"
        ));

        // '빽빽'한 여행과 '널널'한 여행에 따라 리스트를 다르게 처리
        if (!density) { // '널널'한 여행인 경우
            firstDayActivities = new ArrayList<>(List.of(
                    "식당-식당명:구",
                    "관광지-관광지명:구",
                    "식당-식당명:구",
                    "관광지-관광지명:구"
            ));
            middleDayActivities = new ArrayList<>(List.of(
                    "관광지-관광지명:구",
                    "식당-식당명:구",
                    "관광지-관광지명:구",
                    "카페-카페명:구",
                    "식당-식당명:구"
            ));
            lastDayActivities = new ArrayList<>(List.of(
                    "관광지-관광지명:구",
                    "식당-식당명:구",
                    "카페-카페명:구"
            ));
        }

        // 중간 날들의 활동 리스트를 생성
        StringBuilder middleDaysActivities = new StringBuilder();
        for (int i = 2; i < days; i++) {
            middleDaysActivities.append(String.format("%d일차\n%s\n\n", i, String.join("\n", middleDayActivities)));
        }

        return String.format(
                "서울 여행을 %s에, %d일동안 갈 예정이야. 실제 한국인들이 방문하는 구체적인 장소명을," +
                        " <%s, %s, %s>에 해당하는 장소를 포함해서 알려줘. 카페는 프랜차이즈가 아닌 곳으로 추천해줘." +
                        " 답변형식은 반드시 \"관광지-장소명:구\", \"식당-식당명:구\",\"카페-카페명:구\" 형식으로 답변해줘." +
                        " 다른 추가적인 말은 일체 하지말고 답변형식만 지켜줘.\n" +
                        "1일차\n" +
                        "%s\n\n" + // 첫날 활동 리스트를 넣는 부분
                        "%s" +     // 중간 날짜들 활동을 넣는 부분
                        "%d일차\n" +
                        "%s",      // 마지막 날 활동 리스트를 넣는 부분
                season,                              // 계절
                days,                                // 여행 일수
                theme1, theme2, theme3,              // 테마1, 테마2, 테마3
                String.join("\n", firstDayActivities),  // 첫날 활동 리스트를 줄바꿈으로 연결
                middleDaysActivities.toString(),  // 중간 날짜들 활동 리스트
                days,                                // 마지막 날 (여행 일자)
                String.join("\n", lastDayActivities)   // 마지막 날 활동 리스트 줄바꿈으로 연결
        );
    }



    private String gptQuestion2(boolean density, LocalDate firstDate, LocalDate lastDate, String theme1, String theme2, String theme3, float totalFirstDayTime, float totalLastDayTime) throws IOException {
        // 두 날짜 사이의 차이를 Period 객체로 반환
        int days = firstDate.until(lastDate).getDays() + 1;
        String season = Season(firstDate.getMonthValue());

        // 첫날 활동 리스트
        List<String> firstDayActivities = new ArrayList<>(List.of(
                "식당-식당명:구",
                "관광지-관광지명:구",
                "카페-카페명:구",
                "관광지-관광지명:구",
                "식당-식당명:구",
                "관광지-관광지명:구",
                "카페-카페명:구"
        ));

        // 중간 날짜들 활동 리스트
        List<String> middleDayActivities = new ArrayList<>(List.of(
                "관광지-관광지명:구",
                "식당-식당명:구",
                "관광지-관광지명:구",
                "카페-카페명:구",
                "관광지-관광지명:구",
                "식당-식당명:구",
                "관광지-관광지명:구"
        ));

        // 마지막 날 활동 리스트
        List<String> lastDayActivities = new ArrayList<>(List.of(
                "관광지-관광지명:구",
                "식당-식당명:구",
                "카페-카페명:구",
                "관광지-관광지명:구",
                "식당-식당명:구",
                "관광지-관광지명:구",
                "카페-카페명:구"
        ));

        // 첫날과 마지막 날에 추가할 활동 개수 계산
        int firstDayCount = calculateTaskCount(totalFirstDayTime, density); // 총 활동 개수를 2로 나눈 값에 따라 결정
        int lastDayCount = calculateTaskCount(totalLastDayTime, density);

        // '빽빽'한 여행과 '널널'한 여행에 따라 리스트를 다르게 처리
        if (!density) { // '널널'한 여행인 경우
            firstDayActivities = new ArrayList<>(List.of(
                    "식당-식당명:구",
                    "관광지-관광지명:구",
                    "카페-카페명:구",
                    "관광지-관광지명:구",
                    "식당-식당명:구"
            ));
            middleDayActivities = new ArrayList<>(List.of(
                    "관광지-관광지명:구",
                    "식당-식당명:구",
                    "관광지-관광지명:구",
                    "카페-카페명:구",
                    "식당-식당명:구"
            ));
            lastDayActivities = new ArrayList<>(List.of(
                    "관광지-관광지명:구",
                    "식당-식당명:구",
                    "카페-카페명:구",
                    "관광지-관광지명:구",
                    "식당-식당명:구"
            ));
        }

        // 첫날과 마지막 날에서 필요한 활동만큼 추출
        List<String> finalFirstDayActivities = firstDayActivities.subList(0, Math.min(firstDayCount, firstDayActivities.size()));
        List<String> finalLastDayActivities = lastDayActivities.subList(0, Math.min(lastDayCount, lastDayActivities.size()));

        // 중간 날들의 활동 리스트를 생성
        StringBuilder middleDaysActivities = new StringBuilder();
        for (int i = 2; i < days; i++) {
            middleDaysActivities.append(String.format("%d일차\n%s\n\n", i, String.join("\n", middleDayActivities)));
        }

        // GPT에게 보낼 질문 생성
        return String.format(
                "서울 여행을 %s에, %d일동안 갈 예정이야. 실제 한국인들이 방문하는 구체적인 장소명을," +
                        " <%s, %s, %s>에 해당하는 장소를 포함해서 알려줘. 카페는 프랜차이즈가 아닌 곳으로 추천해줘." +
                        " 답변형식은 반드시 \"관광지-장소명:구\", \"식당-식당명:구\",\"카페-카페명:구\" 형식으로 답변해줘." +
                        " 다른 추가적인 말은 일체 하지말고 답변형식만 지켜줘.\n" +
                        "1일차\n" +
                        "%s\n" + // 첫날 활동 리스트를 넣는 부분
                        "\n" +
                        "%s" +  // 중간 날 활동 리스트를 넣는 부분
                        "%d일차\n" +
                        "%s", // 마지막 날 활동 리스트를 넣는 부분
                season,                              // 계절
                days,                            // 여행 일수
                theme1, theme2, theme3,              // 테마1, 테마2, 테마3
                String.join("\n", finalFirstDayActivities),  // 첫날 활동 리스트를 줄바꿈으로 연결
                middleDaysActivities.toString(),     // 중간 날짜 활동 리스트 줄바꿈으로 연결
                days,                            // 마지막 날 (여행 일자)
                String.join("\n", finalLastDayActivities)   // 마지막 날 활동 리스트 줄바꿈으로 연결
        );
    }


    // 월에 따른 계절 계산
    private String Season(int month) {
        if (month == 12 || month == 1 || month == 2) return "겨울";
        else if (month == 3 || month == 4 || month == 5) return "봄";
        else if (month == 6 || month == 7 || month == 8) return "여름";
        else return "가을";
    }

    // DB와의 유사성 확인을 위한 파이썬 스크립트 실행
    private String checkSimilarity(String name, String address) throws IOException {
        String pythonInterpreter = "python";
        String simPythonScriptPath = "C:/vscode/api_practice/similarity.py";
        ProcessBuilder simProcessBuilder = new ProcessBuilder(pythonInterpreter, simPythonScriptPath, name, address);
        simProcessBuilder.redirectErrorStream(true);
        Process simProcess = simProcessBuilder.start();
        // 파이썬에서 일치하면 placeId를 출력, 불일치하면 "일치하는 정보 없음" 출력. 그 값을 읽어옴.
        BufferedReader simReader = new BufferedReader(new InputStreamReader(simProcess.getInputStream()));
        return simReader.readLine().trim(); // placeId or "일치하는 정보 없음" 반환
    }

    private String changeTour(String address, String theme1, String theme2, String theme3, List<String> existingPlaceIds) throws IOException {
        List<String> themes = Arrays.asList(theme1, theme2, theme3);
        List<String> placeIds = placeInfoRepository.findPlaceIdsByAddressAndThemes(address, themes);

        placeIds.removeAll(existingPlaceIds);

        if (!placeIds.isEmpty()) {
            // 랜덤으로 하나 선택
            Random random = new Random();
            return placeIds.get(random.nextInt(placeIds.size()));
        } else {
            // 일치하는 테마가 없을 경우 '식당'과 '카페'가 아닌 곳 중 랜덤으로 하나 선택
            List<String> alternativePlaceIds = placeInfoRepository.findAlternativePlaceIds(address);
            if (!alternativePlaceIds.isEmpty()) {
                Random random = new Random();
                return alternativePlaceIds.get(random.nextInt(alternativePlaceIds.size()));
            } else {
                return "자료 없음"; // 해당 구에 일치하는 곳이 없을 때
            }
        }
    }

    // 식당이나 카페를 찾는 메서드
    private String changeFoodOrCafe(String address, String placeType, List<String> existingPlaceIds) {
        List<String> placeIds = placeInfoRepository.findRestaurantOrCafePlaceIds(address, placeType);

        // 기존에 추천된 장소를 제외한 리스트 생성
        placeIds.removeAll(existingPlaceIds);

        if (!placeIds.isEmpty()) {
            // 랜덤으로 하나 선택
            Random random = new Random();
            return placeIds.get(random.nextInt(placeIds.size()));
        } else {
            return "자료 없음"; // 해당 구에 일치하는 곳이 없을 때
        }
    }




    // 활동 개수 계산 함수 (0.5 단위로 조정)
    private int calculateTaskCount(float totalTime, boolean density) throws IOException {
        if(density) {
            if (totalTime / 2 >= 6.5) {
                return 7;
            } else if (totalTime / 2 >= 5.5) {
                return 6;
            } else if (totalTime / 2 >= 4.5) {
                return 5;
            } else if (totalTime / 2 >= 3.5) {
                return 4;
            } else if (totalTime / 2 >= 2.5) {
                return 3;
            } else if (totalTime / 2 >= 1.5) {
                return 2;
            } else if (totalTime / 2 >= 0.5) {
                return 1;
            } else {
                return 0; // 0.5 미만일 경우 활동 없음
            }
        } else {
            if (totalTime / 3 >= 6.5) {
                return 7;
            } else if (totalTime / 3 >= 5.5) {
                return 6;
            } else if (totalTime / 3 >= 4.5) {
                return 5;
            } else if (totalTime / 3 >= 3.5) {
                return 4;
            } else if (totalTime / 3 >= 2.5) {
                return 3;
            } else if (totalTime / 3 >= 1.5) {
                return 2;
            } else if (totalTime / 3 >= 0.5) {
                return 1;
            } else {
                return 0; // 0.5 미만일 경우 활동 없음
            }
        }
    }
}
