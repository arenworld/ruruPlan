package net.datasa.ruruplan.gpt.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.gpt.domain.dto.GptResultDTO;
import net.datasa.ruruplan.gpt.domain.entity.GptCmdEntity;
import net.datasa.ruruplan.gpt.repository.GptCmdRepository;
import net.datasa.ruruplan.plan.domain.dto.PlaceInfoDTO;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import net.datasa.ruruplan.plan.domain.dto.TaskDTO;
import net.datasa.ruruplan.plan.domain.entity.PlaceInfoEntity;
import net.datasa.ruruplan.plan.repository.PlaceInfoRepository;
import net.datasa.ruruplan.plan.repository.PlanRepository;
import net.datasa.ruruplan.plan.repository.TaskRepository;
import net.datasa.ruruplan.plan.repository.impl.PlaceInfoRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GptResultService {

    private final GptCmdRepository gptCmdRepository;
    private final PlanRepository planRepository;
    private final TaskRepository taskRepository;

    @Autowired
    private PlaceInfoRepository placeInfoRepository;



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
        String simPythonScriptPath = "src/test/python/similarity.py";
        ProcessBuilder simProcessBuilder = new ProcessBuilder(pythonInterpreter, simPythonScriptPath, name, address);
        simProcessBuilder.redirectErrorStream(true);
        Process simProcess = simProcessBuilder.start();
        // 파이썬에서 일치하면 placeId를 출력, 불일치하면 "일치하는 정보 없음" 출력. 그 값을 읽어옴.
        BufferedReader simReader = new BufferedReader(new InputStreamReader(simProcess.getInputStream()));
        return simReader.readLine().trim(); // placeId or "일치하는 정보 없음" 반환
    }

    // 관광지 데이터가 없을때 대체 데이터 가져오는 메서드
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

    // DB에 데이터가 없을 때 대체할 데이터 찾는 메서드
    private String changeFoodOrCafe(String address, String placeType, List<String> existingPlaceIds) {
        List<String> placeIds = placeInfoRepository.findRestaurantOrCafePlaceIds(address, placeType);

        // 기존에 추천된 장소를 제외한 리스트 생성
        placeIds.removeAll(existingPlaceIds);

        if (!placeIds.isEmpty()) {
            // 랜덤으로 하나 선택
            Random random = new Random();
            return placeIds.get(random.nextInt(placeIds.size()));
        } else {
            log.debug("해당 구가 없거나 해당 구에 같은 테마를 가진 장소가 없음. : {}", address);
            // 테마로만 일치하는 랜덤 장소 가져오기
            String randomPlaceId = getRandomPlaceByTheme(placeType, existingPlaceIds);
            log.debug("랜덤 추천된 장소 ID: {}", randomPlaceId);
            return randomPlaceId;
        }
    }

    // 테마 1, 2, 3 중 하나라도 일치하는 장소 중 랜덤으로 가져오는 메서드
    private String getRandomPlaceByTheme(String placeType, List<String> existingPlaceIds) {
        List<String> allMatchingPlaceIds = placeInfoRepository.findByThemeAndExcludeExistingPlaces(placeType, existingPlaceIds);

        if (!allMatchingPlaceIds.isEmpty()) {
            Random random = new Random();
            String randomPlaceId = allMatchingPlaceIds.get(random.nextInt(allMatchingPlaceIds.size()));
            log.debug("랜덤으로 선택된 placeId: {}", randomPlaceId);
            return randomPlaceId;
        } else {
            log.debug("일치하는 테마를 가진 장소가 없음.");
            return "자료 없음"; // 만약 장소가 없으면 이 값을 반환
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




    public PlanDTO planCreate(GptResultDTO result) throws IOException {
        Integer cmdNum = result.getCmdNum();
        List<String> placeIdList = result.getPlaceIdList();

        log.debug("cmdNum: {}", cmdNum);
        log.debug("placeIdList: {}", placeIdList);

        // GptCmdEntity에서 필요한 정보를 불러옴
        GptCmdEntity gptCmdEntity = gptCmdRepository.findById(cmdNum)
                .orElseThrow(() -> new EntityNotFoundException("cmdNum으로 Entity를 불러오지 못했습니다: " + cmdNum));

        LocalDate firstDate = gptCmdEntity.getFirstDate();
        LocalDate lastDate = gptCmdEntity.getLastDate();
        LocalTime arrival = gptCmdEntity.getArrival();
        LocalTime depart = gptCmdEntity.getDepart();
        String theme1 = gptCmdEntity.getTheme1();
        String theme2 = gptCmdEntity.getTheme2();
        String theme3 = gptCmdEntity.getTheme3();
        boolean density = gptCmdEntity.getDensity();

        // 계획 생성 (PlanDTO)
        PlanDTO planDTO = PlanDTO.builder()
                .planNum(cmdNum)  // cmdNum을 임시로 planNum으로 사용 나중에 저장하게되면 이 값은 빼고 저장
                .planName("Gpt_Plan")  // 임시 이름
                .cmdNum(cmdNum)
                .memberId(gptCmdEntity.getMember().getMemberId())
                .startDate(firstDate)
                .endDate(lastDate)
                .theme1(theme1)
                .theme2(theme2)
                .theme3(theme3)
                .planCreateDate(LocalDateTime.now())  // 현재 시간으로 생성
                .planUpdateDate(LocalDateTime.now())  // 현재 시간으로 생성
                .taskList(new ArrayList<>())  // taskList는 빈 리스트로 초기화
                .build();

        log.debug("planDTO: {}", planDTO);

        int totalDays = firstDate.until(lastDate).getDays() + 1;  // 총 여행 일 수
        int placeIndex = 0;
        int taskNum = 1;  // taskNum을 계속 증가시키기 위한 변수

        // 각 날의 순서 설정 사용자가 선택한 조건에 따라 달라짐.
        List<String> firstDayOrder;
        List<String> middleDayOrder;
        List<String> lastDayOrder;

        // 활동 수 구하기
        float totalFirstDayTime = 0;
        float totalLastDayTime = 0;
        int firstDayTaskCount = 0;
        int lastDayTaskCount = 0;
        int middleDayTaskCount = 7;  // 기본 중간 날 활동 수는 7개 (조건에 따라 다름)

        // 비행기 시간 입력 했을 때
        if (arrival != null && depart != null) {
            float floatArrival = arrival.getHour() + arrival.getMinute() / 60.0f;
            float floatDepart = depart.getHour() + depart.getMinute() / 60.0f;

            if (density) {
                totalFirstDayTime = 22 - floatArrival - 3;
                totalLastDayTime = floatDepart - 10 - 3;
                firstDayTaskCount = calculateTaskCount(totalFirstDayTime, true);  // 첫 날 활동 개수
                lastDayTaskCount = calculateTaskCount(totalLastDayTime, true);    // 마지막 날 활동 개수

                // 첫날, 중간날, 마지막날 활동 순서 설정 (density=true)
                firstDayOrder = Arrays.asList("식당", "관광지", "카페", "관광지", "식당", "관광지", "카페");
                middleDayOrder = Arrays.asList("관광지", "식당", "관광지", "카페", "관광지", "식당", "관광지");
                lastDayOrder = Arrays.asList("관광지", "식당", "카페", "관광지", "식당", "관광지", "카페");
            } else {
                totalFirstDayTime = 21 - floatArrival - 3;
                totalLastDayTime = floatDepart - 10 - 3;
                firstDayTaskCount = calculateTaskCount(totalFirstDayTime, false); // 첫 날 활동 개수
                lastDayTaskCount = calculateTaskCount(totalLastDayTime, false);   // 마지막 날 활동 개수
                middleDayTaskCount = 5;  // 중간 날 활동 수는 5개로 변경 (density=false인 경우)

                // 첫날, 중간날, 마지막날 활동 순서 설정 (density=false)
                firstDayOrder = Arrays.asList("식당", "관광지", "카페", "관광지", "식당");
                middleDayOrder = Arrays.asList("관광지", "식당", "관광지", "카페", "식당");
                lastDayOrder = Arrays.asList("관광지", "식당", "카페", "관광지", "식당");
            }
        } else {
            // 비행기 시간 정보 없을 때
            if (density) {
                firstDayTaskCount = 6;  // 첫 날 6개
                middleDayTaskCount = 7;  // 중간 날 7개
                lastDayTaskCount = 3;  // 마지막 날 3개

                // 첫날, 중간날, 마지막날 활동 순서 설정 (arrival=null, depart=null, density=true)
                firstDayOrder = Arrays.asList("식당", "관광지", "카페", "관광지", "식당", "관광지");
                middleDayOrder = Arrays.asList("관광지", "식당", "관광지", "카페", "관광지", "식당", "관광지");
                lastDayOrder = Arrays.asList("관광지", "식당", "관광지");
            } else {
                firstDayTaskCount = 4;  // 첫 날 4개
                middleDayTaskCount = 5;  // 중간 날 5개
                lastDayTaskCount = 3;  // 마지막 날 3개

                // 첫날, 중간날, 마지막날 활동 순서 설정 (arrival=null, depart=null, density=false)
                firstDayOrder = Arrays.asList("식당", "관광지", "카페", "관광지");
                middleDayOrder = Arrays.asList("관광지", "식당", "관광지", "카페", "식당");
                lastDayOrder = Arrays.asList("관광지", "식당", "카페", "관광지", "식당");
            }
        }

// 첫 날 TaskDTO 생성 (이동 Task 포함)
        for (int i = 0; i < firstDayTaskCount && placeIndex < placeIdList.size(); i++) {
            // 일반 활동 추가
            createAndAddTask(planDTO, cmdNum, firstDate, 1, placeIdList.get(placeIndex++), taskNum++, firstDayOrder.get(i));

            // 첫 번째 활동이 아니고 마지막 활동이 아닌 경우에만 '이동' 추가
            if (i >= 0 && i < firstDayTaskCount - 1) {
                createAndAddTask(planDTO, cmdNum, firstDate, 1, null, taskNum++, "이동");
            }
        }

// 중간 날 TaskDTO 생성 (이동 Task 포함)
        for (int day = 2; day < totalDays && placeIndex < placeIdList.size(); day++) {
            for (int i = 0; i < middleDayTaskCount && placeIndex < placeIdList.size(); i++) {
                // 일반 활동 추가
                createAndAddTask(planDTO, cmdNum, firstDate.plusDays(day - 1), day, placeIdList.get(placeIndex++), taskNum++, middleDayOrder.get(i));

                // 첫 번째 활동이 아니고 마지막 활동이 아닌 경우에만 '이동' 추가
                if (i >= 0 && i < middleDayTaskCount - 1) {
                    createAndAddTask(planDTO, cmdNum, firstDate.plusDays(day - 1), day, null, taskNum++, "이동");
                }
            }
        }

// 마지막 날 TaskDTO 생성 (이동 Task 포함)
        for (int i = 0; i < lastDayTaskCount && placeIndex < placeIdList.size(); i++) {
            // 일반 활동 추가
            createAndAddTask(planDTO, cmdNum, lastDate, totalDays, placeIdList.get(placeIndex++), taskNum++, lastDayOrder.get(i));

            // 첫 번째 활동이 아니고 마지막 활동이 아닌 경우에만 '이동' 추가
            if (i >= 0 && i < lastDayTaskCount - 1) {
                createAndAddTask(planDTO, cmdNum, lastDate, totalDays, null, taskNum++, "이동");
            }
        }

        return planDTO;  // 생성된 PlanDTO 반환
    }

    // TaskDTO 생성 및 PlanDTO에 추가하는 함수
    private void createAndAddTask(PlanDTO planDTO, Integer planNum, LocalDate date, int dayNum, String placeId, int index, String taskType) {
        // '이동'일 경우에는 PlaceInfoEntity가 필요하지 않으므로 placeId가 null이면 바로 TaskDTO를 생성
        PlaceInfoDTO placeInfoDTO = null;

        if (placeId != null) {
            // placeId로 PlaceInfoDTO를 가져옴
            log.debug("placeId: {}", placeId);
            PlaceInfoEntity placeInfoEntity = placeInfoRepository.findById(placeId)
                    .orElseThrow(() -> new EntityNotFoundException("placeId로 장소를 찾을 수 없습니다: " + placeId));
            placeInfoDTO = PlaceInfoDTO.builder()
                    .placeId(placeInfoEntity.getPlaceId())
                    .titleKr(placeInfoEntity.getTitleKr())
                    .titleJp(placeInfoEntity.getTitleJp())
                    .addressKr(placeInfoEntity.getAddressKr())
                    .addressJp(placeInfoEntity.getAddressJp())
                    .mapX(placeInfoEntity.getMapX())
                    .mapY(placeInfoEntity.getMapY())
                    .siGunGu(placeInfoEntity.getSiGunGu())
                    .contentsType(placeInfoEntity.getContentsType())
                    .theme1(placeInfoEntity.getTheme1())
                    .theme2(placeInfoEntity.getTheme2())
                    .theme3(placeInfoEntity.getTheme3())
                    .petFriendly(placeInfoEntity.getPetFriendly())
                    .barrierFree(placeInfoEntity.getBarrierFree())
                    .overviewKr(placeInfoEntity.getOverviewKr())
                    .overviewJp(placeInfoEntity.getOverviewJp())
                    .heritage(placeInfoEntity.getHeritage())
                    .infocenter(placeInfoEntity.getInfocenter())
                    .usetimeKr(placeInfoEntity.getUsetimeKr())
                    .usetimeJp(placeInfoEntity.getUsetimeJp())
                    .restdateKr(placeInfoEntity.getRestdateKr())
                    .restdateJp(placeInfoEntity.getRestdateJp())
                    .fee(placeInfoEntity.getFee())
                    .feeInfoKr(placeInfoEntity.getFeeInfoKr())
                    .feeInfoJp(placeInfoEntity.getFeeInfo_Jp())
                    .saleItemKr(placeInfoEntity.getSaleItemKr())
                    .saleItemJp(placeInfoEntity.getSaleItemJp())
                    .originImgUrl(placeInfoEntity.getOriginImgUrl())
                    .smallImgUrl(placeInfoEntity.getSmallImgUrl())
                    .build();

            log.debug("placeInfoDTO : {}", placeInfoDTO);
        }

        // TaskDTO 생성
        TaskDTO task = TaskDTO.builder()
                .taskNum(index + 1)  // 활동 번호 (단순 인덱스)
                .planNum(planNum)
                .place(placeInfoDTO)  // '이동'일 경우에는 placeInfoDTO가 null
                .memberId(planDTO.getMemberId())
                .dateN(dayNum)
                .task(taskType)  // 활동 유형 (식당, 관광지, 카페, 이동)
                .cost(0)  // 비용은 일단 0으로 설정
                .memo("")
                .build();

        log.debug("task : {}", task.getTask());

        // PlanDTO의 taskList에 추가
        planDTO.getTaskList().add(task);
    }

}
