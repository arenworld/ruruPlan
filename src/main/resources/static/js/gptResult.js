$(document).ready(async function(){
    console.log("Document is ready");

    // 인쇄 버튼 클릭 이벤트 핸들러
    $('#print-button').click(function(){
        window.print();
    });

    var startX;
    var startY;
    var endX;
    var endY;
    var taskElements = $('td[id^="duration-"]');
    var totalIndex = 0; // 누적 인덱스 초기화

    // Duration 값을 저장할 배열 선언
    var durationsList = [];

    for (var date in taskByDateMap) {
        if (taskByDateMap.hasOwnProperty(date)) {
            var tasksForDate = taskByDateMap[date]; // 날짜에 해당하는 task 리스트
            for (let index = 0; index < tasksForDate.length; index++) {
                var task = tasksForDate[index];

                if(task.task == '이동') {
                    totalIndex++;
                    continue;
                }

                if(index == 0 && task.task != '이동') {
                    startX = task.place.mapX;
                    console.log("출발지 X좌표: ", startX);
                    startY = task.place.mapY;
                    console.log("출발지 Y좌표: ", startY);
                    totalIndex++;
                    continue;
                } else {
                    endX = task.place.mapX;
                    console.log("도착지 X좌표: ", endX);
                    endY = task.place.mapY;
                    console.log("도착지 Y좌표: ", endY);

                    // 좌표 값이 유효한지 확인
                    if (!startX || !startY || !endX || !endY) {
                        console.error('좌표 값이 유효하지 않습니다:', {
                            startX, startY, endX, endY
                        });
                        totalIndex++;
                        continue;
                    }

                    // 변수 값 캡처
                    let currentIndex = totalIndex;
                    let sx = startX;
                    let sy = startY;
                    let ex = endX;
                    let ey = endY;

                    totalIndex++; // 다음 인덱스를 위해 증가

                    try {
                        // 도보 시간 가져오기
                        let duration1 = await walking(sx, sy, ex, ey);
                        console.log("도보시간: " + duration1);
                        let durationFloat = parseFloat(duration1);

                        if(durationFloat > 20) {
                            // 대중교통 정보 가져오기
                            let routeInfo = await busSubway(sx, sy, ex, ey);
                            console.log("대중교통 종류: " + routeInfo.subPaths[1].trafficType);
                            let trafficType;
                            if(routeInfo.subPaths[1].trafficType == 1) trafficType = "지하철";
                            else if(routeInfo.subPaths[1].trafficType == 2) trafficType = "버스";
                            else trafficType = "도보";
                            taskElements.eq(currentIndex - 1).html(trafficType + " " + routeInfo.totalTime + "분");

                            durationsList.push({
                                taskNum: task.taskNum,
                                duration: routeInfo.totalTime
                            });
                        } else {
                            taskElements.eq(currentIndex - 1).html("도보 " + duration1 + "분");

                            durationsList.push({
                                taskNum: task.taskNum,
                                duration: duration1
                            });
                        }
                    } catch (error) {
                        console.error("오류 발생:", error);
                    }

                    // 다음 출발지 좌표 설정
                    startX = endX;
                    startY = endY;
                }
            }
        }
    }
    console.log("duration 정보: " + durationsList);

    async function walking(startX, startY, endX, endY) {
        return new Promise(function(resolve, reject) {
            var headers = {};
            headers["appKey"] = "7ejrjQSxsM8Vp5U8WbLArOuHpOwQNnJ31hqE3Pt7"; // 실제 API 키로 대체하세요.

            console.log("도보메서드에 전달된 좌표 값:", startX, startY, endX, endY);

            $.ajax({
                method: "POST",
                headers: headers,
                url: "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&format=json",
                data: {
                    "startX": startX,
                    "startY": startY,
                    "endX": endX,
                    "endY": endY,
                    "reqCoordType": "WGS84GEO",
                    "resCoordType": "EPSG3857",
                    "startName": "출발지",
                    "endName": "도착지"
                },
                success: function (response) {
                    var resultData = response.features;
                    var tTime = ((resultData[0].properties.totalTime) / 60).toFixed(0);
                    console.log("도보 시간: " + tTime);
                    resolve(tTime);
                },
                error: function(xhr, status, error) {
                    console.error("도보 경로 요청 실패:", error);
                    reject(error);
                }
            });
        });
    }

    async function busSubway(startX, startY, endX, endY) {
        return new Promise(function(resolve, reject) {
            let sx = startX;
            let sy = startY;
            let ex = endX;
            let ey = endY;

            console.log("대중교통 메서드에 전달된 좌표 값:", sx, sy, ex, ey);

            var xhr = new XMLHttpRequest();
            var url = "https://api.odsay.com/v1/api/searchPubTransPathT?SX=" + sx + "&SY=" + sy +
                "&EX=" + ex + "&EY=" + ey + "&apiKey=AbfWDSywAKWcKBRv/ClpFQ";

            xhr.open("GET", url, true);
            xhr.send();

            xhr.onreadystatechange = function() {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        var responseText = xhr.responseText;
                        var data = JSON.parse(responseText);

                        // 에러 응답 처리
                        if (!data.result || !data.result.path) {
                            console.error("API 응답에 'result' 또는 'path' 속성이 없습니다.", data);
                            reject(new Error("대중교통 경로를 찾을 수 없습니다."));
                            return;
                        }

                        // 가장 빠른 경로 찾기
                        const fastestRoute = data.result.path.reduce((min, current) => {
                            return current.info.totalTime < min.info.totalTime ? current : min;
                        });

                        // 필요한 정보 추출
                        const routeInfo = {
                            totalTime: fastestRoute.info.totalTime,
                            subPaths: fastestRoute.subPath.map(subPath => {
                                return {
                                    trafficType: subPath.trafficType,  // 교통수단 타입 (1: 지하철, 2: 버스, 3: 도보)
                                    startName: subPath.startName || '', // 시작역 또는 정류장 이름
                                    endName: subPath.endName || ''      // 종료역 또는 정류장 이름
                                };
                            })
                        };
                        console.log("대중교통 시간: " + routeInfo.totalTime);
                        resolve(routeInfo);
                    } else {
                        console.error("대중교통 경로 요청 실패:", xhr.status);
                        reject(new Error("대중교통 경로를 가져오는 중 오류가 발생했습니다."));
                    }
                }
            };
        });
    }
});

