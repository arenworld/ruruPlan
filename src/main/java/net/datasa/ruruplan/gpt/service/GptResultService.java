package net.datasa.ruruplan.gpt.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public List<String> gptApi(Integer cmdNum) throws IOException {

        GptCmdEntity gptCmdEntity = gptCmdRepository.findById(cmdNum).orElseThrow(() -> new EntityNotFoundException("cmdNum으로 Entity를 불러오지 못했습니다: " + cmdNum));

        // GptCmdDTO에서 세부 정보를 추출
        LocalDate firstDate = gptCmdEntity.getFirstDate();
        LocalDate lastDate = gptCmdEntity.getLastDate();
        LocalTime arrival = gptCmdEntity.getArrival();
        float floatArrival = arrival.getHour() + arrival.getMinute() / 60.0f;
        LocalTime depart = gptCmdEntity.getDepart();
        float floatDepart = depart.getHour() + depart.getMinute() / 60.0f;
        String theme1 = gptCmdEntity.getTheme1();
        String theme2 = gptCmdEntity.getTheme2();
        String theme3 = gptCmdEntity.getTheme3();
        boolean density = gptCmdEntity.getDensity();

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


        // 파이썬 스크립트 경로 설정
        String firstPythonScriptPath = "../test/python/newQuestion.py";
        String secondPythonScriptPath = "../test/python/followingQuestion.py";
        String pythonInterpreter = "python";

        try {
            // 첫 번째 파이썬 스크립트 실행
            ProcessBuilder processBuilder = new ProcessBuilder(pythonInterpreter, firstPythonScriptPath, argument);
            Process process = processBuilder.start();
            // 파이썬에서 출력된 값 읽어오는 객체 생성
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int retryCount = 0; // 추가된 부분: 재시도 카운트 초기화

            // GPT 출력 값을 한 줄씩 읽기
            while ((line = reader.readLine()) != null) {
                // ':'을 기준으로 나눠서 배열에 저장(앞에는 장소명, 뒤에는 '~구' 형태의 주소)
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    String name = parts[0].trim();
                    String address = parts[1].trim();

                    // DB 일치 여부 확인 및 placeId 반환
                    String placeId = checkSimilarity(name, address);
                    if (placeId.equals("일치하는 정보 없음")) {
                        // 일치하지 않을 경우 최대 5번 재시도
                        while (retryCount < 5) {
                            // 파이썬에게 재질문하는 메서드 실행 및 반환된 정보(ex. 경복궁:종로구) 저장
                            String newRecommendation = retryRecommendation(name, address, secondPythonScriptPath, pythonInterpreter);
                            // 일치여부 다시 확인 및 출력내역 저장
                            String newPlaceId = checkSimilarity(newRecommendation.split(":")[0].trim(), newRecommendation.split(":")[1].trim());
                            if (!newPlaceId.equals("일치하는 정보 없음")) {
                                placeId = newPlaceId;
                                break; // 일치할 경우 루프 종료
                            }
                            // 반복횟수
                            retryCount++;
                        }
                        // 5번 시도 후에도 일치하지 않으면 "자료 없음" 저장
                        if (retryCount == 5) {
                            placeId = "자료 없음";
                        }
                    }
                    // placeId를 리스트에 추가
                    placeIdList.add(placeId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("place_id: {}", placeIdList);
        return placeIdList; // 변경된 반환 값 (placeId 리스트 반환)
    }


    // 여행 테마와 일정 기반으로 GPT에게 전달할 '질문' 구조 만들기
    private String gptQuestion(boolean density, LocalDate firstDate, LocalDate lastDate, String theme1, String theme2, String theme3) {
        int day = firstDate.until(lastDate).getDays();
        String season = Season(firstDate.getMonthValue());

        // 밀도(density)에 따른 장소 개수 결정
        int firstDayTour = density ? 3 : 1;
        int firstDayFood = density ? 2 : 1;
        int firstDayCafe = density ? 1 : 0;
        int middleDayTour = density ? 4 : 2;
        int middleDayFood = 2;
        int middleDayCafe = 1;
        int lastDayTour = density ? 2 : 1;
        int lastDayFood = 1;
        int lastDayCafe = density ? 0 : 1;

        // 텍스트 포맷팅하여 GPT에게 전달할 인자 생성 (기존 구조 유지)
        return String.format(
                    "서울 여행을 %s 에 %d 일동안 할건데 서울의 관광지와 식당을 내가 알려줄 테마를 기반으로 장소이름과 주소로 추천해줘. " +
                    "내가 원하는 관광지의 테마는 %s, %s, %s 이렇게 세 개야. 적절히 분배해서 일정 중에 세 가지 테마가 한 번 이상은 반드시 들어가게 해줘. " +
                    "주소는 '~구' 만 알려주면 돼. 첫 날은 식당은 %d개, 관광지는 %d개, 카페는 %d개 추천해줘. " +
                    "마지막 날은 식당은 %d개, 관광지는 %d개, 카페는 %d개 추천해줘. 나머지 날에는 식당 %d개, 관광지 %d개, 카페는 %d개를 추천해줘. " +
                    "카페는 프랜차이즈가 아닌곳으로 추천해줘. 이 모든 일정은 이동동선을 고려해서 만들어줘. 모두 '한국어'로 답해줘. " +
                    "줄 나누기는 하고, 띄어쓰기는 하지 말아줘.예시도 보여줄게. 답변에는 '식당1:~' 이런식으로 쓰면 절대 안돼. 그리고 형식을 반드시 지켜줘. 형식 외에 다른 말은 절대 절대 절대 하지 말아줘.\n" +
                    "{\n" +
                    "ex)\n" +
                    "1일차\n" +
                    "토속촌삼계탕:종로구\n" +
                    "경복궁:종로구\n" +
                    "북촌한옥마을:종로구\n" +
                    "}\n\n" +
                    "1일차\n" +
                    "식당이름:구\n" +
                    "관광지이름:구\n" +
                    "카페이름:구\n" +
                    "관광지이름:구\n" +
                    "식당이름:구\n" +
                    "관광지이름:구\n\n" +
                    "N일차\n" +
                    "관광지이름:구\n" +
                    "식당이름:구\n" +
                    "관광지이름:구\n" +
                    "카페이름:구\n" +
                    "관광지이름:구\n" +
                    "식당이름:구\n" +
                    "관광지이름:구\n\n" +
                    "%d일차\n" +
                    "관광지이름:구\n" +
                    "식당이름:구\n" +
                    "카페이름:구",
                    season, day, theme1, theme2, theme3, firstDayFood, firstDayTour, firstDayCafe, lastDayFood, lastDayTour, lastDayCafe, middleDayFood, middleDayTour, middleDayCafe, day
                );
    }


    private String gptQuestion2(boolean density, LocalDate firstDate, LocalDate lastDate, String theme1, String theme2, String theme3, float totalFirstDayTime, float totalLastDayTime) throws IOException {
        // 두 날짜 사이의 차이를 Period 객체로 반환
        int day = firstDate.until(lastDate).getDays();
        String season = Season(firstDate.getMonthValue());


// 첫날 활동 리스트
        List<String> firstDayActivities = new ArrayList<>(List.of(
                "식당이름:구",
                "관광지이름:구",
                "카페이름:구",
                "관광지이름:구",
                "식당이름:구",
                "관광지이름:구",
                "카페이름:구"
        ));

// 중간 날짜들 활동 리스트
        List<String> middleDayActivities = new ArrayList<>(List.of(
                "관광지이름:구",
                "식당이름:구",
                "관광지이름:구",
                "카페이름:구",
                "관광지이름:구",
                "식당이름:구",
                "관광지이름:구"
        ));

// 마지막 날 활동 리스트
        List<String> lastDayActivities = new ArrayList<>(List.of(
                "관광지이름:구",
                "식당이름:구",
                "카페이름:구",
                "관광지이름:구",
                "식당이름:구",
                "관광지이름:구",
                "카페이름:구"
        ));

// 첫날과 마지막 날에 추가할 활동 개수 계산
        int firstDayCount = calculateActivityCount(totalFirstDayTime, density); // 총 활동 개수를 2로 나눈 값에 따라 결정
        int lastDayCount = calculateActivityCount(totalLastDayTime, density);

// '빽빽'한 여행과 '널널'한 여행에 따라 리스트를 다르게 처리
        if (!density) { // '널널'한 여행인 경우
            firstDayActivities = new ArrayList<>(List.of(
                    "식당이름:구",
                    "관광지이름:구",
                    "카페이름:구",
                    "관광지이름:구",
                    "식당이름:구"
            ));
            middleDayActivities = new ArrayList<>(List.of(
                    "관광지이름:구",
                    "식당이름:구",
                    "관광지이름:구",
                    "카페이름:구",
                    "식당이름:구"
            ));
            lastDayActivities = new ArrayList<>(List.of(
                    "관광지이름:구",
                    "식당이름:구",
                    "카페이름:구",
                    "관광지이름:구",
                    "식당이름:구"
            ));
        }

        // 첫날과 마지막 날에서 필요한 활동만큼 추출
        List<String> finalFirstDayActivities = firstDayActivities.subList(0, Math.min(firstDayCount, firstDayActivities.size()));
        List<String> finalLastDayActivities = lastDayActivities.subList(0, Math.min(lastDayCount, lastDayActivities.size()));

        return String.format(
                "서울 여행을 %s 에 %d 일동안 할건데 서울의 관광지와 식당을 내가 알려줄 테마를 기반으로 장소이름과 주소로 추천해줘. " +
                        "내가 원하는 관광지의 테마는 %s, %s, %s 이렇게 세 개야. 적절히 분배해서 일정 중에 세 가지 테마가 한 번 이상은 반드시 들어가게 해줘. " +
                        "주소는 '~구' 만 알려주면 돼. 답변 형식에 적힌 순서와 개수에 맞춰서 관광지, 식당, 카페를 추천해주면 돼." +
                        "카페는 프랜차이즈가 아닌곳으로 추천해줘. 이 모든 일정은 이동동선을 고려해서 만들어줘. 모두 '한국어'로 답해줘. " +
                        "줄 나누기는 하고, 띄어쓰기는 하지 말아줘. 예시도 보여줄게. 답변에는 '식당1:~' 이런식으로 쓰면 절대 안돼. 그리고 형식을 반드시 지켜줘. 다른 말은 절대로 하지 말아줘.\n" +
                        "{\n" +
                        "ex)\n" +
                        "1일차\n" +
                        "토속촌삼계탕:종로구\n" +
                        "경복궁:종로구\n" +
                        "북촌한옥마을:종로구\n" +
                        "}\n\n" +
                        "1일차\n" +
                        "%s\n" + // 첫날 활동 리스트를 넣는 부분
                        "\n" +
                        "N일차\n" +
                        "%s\n" + // 중간 날짜 활동 리스트를 넣는 부분
                        "\n" +
                        "%d일차\n" +
                        "%s", // 마지막 날 활동 리스트를 넣는 부분
                season,                              // 계절
                day,                                 // 여행 일수
                theme1, theme2, theme3,              // 테마1, 테마2, 테마3
                String.join("\n", firstDayActivities),  // 첫날 활동 리스트를 줄바꿈으로 연결
                String.join("\n", middleDayActivities), // 중간 날짜 활동 리스트 줄바꿈으로 연결
                day,                                  // 마지막 날 (여행 일자)
                String.join("\n", lastDayActivities)   // 마지막 날 활동 리스트 줄바꿈으로 연결
        );

    }


    // 월에 따른 계절 계산 (기존 구조)
    private String Season(int month) {
        if (month == 12 || month == 1 || month == 2) return "겨울";
        else if (month == 3 || month == 4 || month == 5) return "봄";
        else if (month == 6 || month == 7 || month == 8) return "여름";
        else return "가을";
    }

    // DB와의 유사성 확인을 위한 파이썬 스크립트 실행 (새로 추가된 메소드)
    private String checkSimilarity(String name, String address) throws IOException {
        String simPythonScriptPath = "C:/vscode/api_practice/similarity.py";
        ProcessBuilder simProcessBuilder = new ProcessBuilder("C:/python/python.exe", simPythonScriptPath, name, address);
        simProcessBuilder.redirectErrorStream(true);
        Process simProcess = simProcessBuilder.start();
        // 파이썬에서 일치하면 placeId를 출력, 불일치하면 "일치하는 정보 없음" 출력. 그 값을 읽어옴.
        BufferedReader simReader = new BufferedReader(new InputStreamReader(simProcess.getInputStream()));
        return simReader.readLine().trim(); // placeId or "일치하는 정보 없음" 반환
    }

    // 일치하지 않을 경우 재추천 요청 (추가된 메소드)
    private String retryRecommendation(String name, String address, String secondPythonScriptPath, String pythonInterpreter) throws IOException {
        ProcessBuilder retryProcessBuilder = new ProcessBuilder(pythonInterpreter, secondPythonScriptPath, name, address);
        retryProcessBuilder.redirectErrorStream(true);
        Process retryProcess = retryProcessBuilder.start();
        BufferedReader retryReader = new BufferedReader(new InputStreamReader(retryProcess.getInputStream()));
        return retryReader.readLine().trim(); // 재추천된 값 반환
    }

    // 활동 개수 계산 함수 (0.5 단위로 조정)
    private int calculateActivityCount(float totalTime, boolean density) throws IOException {
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
