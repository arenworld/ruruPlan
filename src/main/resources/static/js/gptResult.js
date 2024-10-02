$(document).ready(async function() {
    console.log("Document is ready");

    $('#print-button').click(function() {
        window.print();
    });

    $('#button2').click(function() {
        window.location.href = '/gptView/loading';
    });

    var totalCost = 0;
    var allTasks = []; // 모든 TaskDTO를 저장할 배열

    // 모든 날짜에 대해 처리
    for (var date in taskByDateMap) {
        if (taskByDateMap.hasOwnProperty(date)) {
            var tasksForDate = taskByDateMap[date]; // 날짜에 해당하는 task 리스트

            var prevStartTime = null;
            var prevDuration = null;
            var startX = null;
            var startY = null;

            for (let index = 0; index < tasksForDate.length; index++) {
                var task = tasksForDate[index];

                // 첫 번째 task 처리
                if (index == 0) {
                    if (task.startTime) {
                        prevStartTime = task.startTime;
                    } else {
                        prevStartTime = (date == '1') ? '14:00' : '10:00';
                        task.startTime = prevStartTime;
                    }

                    $('#time-' + task.taskNum).html(formatTime(prevStartTime));

                    var taskDuration = parseDuration(task.duration); // 분 단위로 변환
                    prevDuration = taskDuration;

                    $('#duration-' + task.taskNum).html(formatDuration(taskDuration));

                    if (task.task != '이동') {
                        startX = task.place.mapX;
                        startY = task.place.mapY;
                    }
                } else {
                    var currentStartTime = addMinutesToTime(prevStartTime, prevDuration);
                    task.startTime = currentStartTime;

                    $('#time-' + task.taskNum).html(formatTime(currentStartTime));

                    if (task.task == '이동') {
                        var prevTask = tasksForDate[index - 1];
                        var nextTask = tasksForDate[index + 1];

                        if (prevTask && nextTask) {
                            var sx = prevTask.place.mapX;
                            var sy = prevTask.place.mapY;
                            var ex = nextTask.place.mapX;
                            var ey = nextTask.place.mapY;

                            if (!sx || !sy || !ex || !ey) {
                                $('#duration-' + task.taskNum).html('0분');
                                prevDuration = 0;
                                continue;
                            }

                            if (sx === ex && sy === ey) {
                                // 출발지와 도착지가 동일한 경우
                                $('#duration-' + task.taskNum).html('0분');
                                $('#cost-' + task.taskNum).html('0원');

                                prevDuration = 0;
                                task.duration = 0;
                                task.cost = 0;
                                task.task = '이동 없음'; // 필요에 따라 task명을 변경

                            } else {
                                try {
                                    let duration;
                                    let transitType;
                                    let cost = 0;

                                    let walkingDuration = await walking(sx, sy, ex, ey);
                                    let durationFloat = parseFloat(walkingDuration);

                                    if (durationFloat > 22) {
                                        let routeInfo = await busSubway(sx, sy, ex, ey);
                                        duration = routeInfo.totalTime;
                                        cost = parseInt(routeInfo.payment);

                                        if (routeInfo.subPaths[1]) {
                                            if (routeInfo.subPaths[1].trafficType == 1) transitType = "대중교통";
                                            else if (routeInfo.subPaths[1].trafficType == 2) transitType = "대중교통";
                                            else transitType = "도보";
                                        } else {
                                            transitType = "도보";
                                        }

                                        $('#duration-' + task.taskNum).html(transitType + " " + duration + "분");
                                        $('#cost-' + task.taskNum).html(routeInfo.payment + '원');

                                        totalCost += cost;

                                        task.duration = duration; // 분 단위로 설정
                                        prevDuration = parseInt(duration);

                                        task.task = transitType;
                                        task.cost = cost;

                                    } else {
                                        duration = durationFloat;
                                        transitType = "도보";
                                        $('#duration-' + task.taskNum).html(transitType + " " + duration + "분");

                                        $('#cost-' + task.taskNum).html('0원');

                                        task.duration = duration;
                                        prevDuration = parseInt(duration);

                                        task.task = transitType;
                                        task.cost = 0;
                                    }

                                } catch (error) {
                                    $('#duration-' + task.taskNum).html('0분');
                                    $('#cost-' + task.taskNum).html('0원');
                                    prevDuration = 0;
                                    task.duration = 0;
                                    task.cost = 0;
                                }
                            }
                        } else {
                            $('#duration-' + task.taskNum).html('0분');
                            $('#cost-' + task.taskNum).html('0원');
                            prevDuration = 0;
                            task.duration = 0;
                            task.cost = 0;
                        }

                    } else {
                        var taskDuration = parseDuration(task.duration);
                        prevDuration = taskDuration;

                        $('#duration-' + task.taskNum).html(formatDuration(taskDuration));

                        startX = task.place.mapX;
                        startY = task.place.mapY;

                        var costText = $('#cost-' + task.taskNum).text();
                        var costNumber = parseInt(costText.replace('원', '').replace(/,/g, ''));
                        if (!isNaN(costNumber)) {
                            totalCost += costNumber;
                            task.cost = costNumber;
                        } else {
                            task.cost = 0;
                        }
                    }

                    prevStartTime = currentStartTime;
                    task.startTime = currentStartTime;
                }

                task.dateN = parseInt(date);
                task.duration = parseDuration(task.duration); // 분 단위로 변환

                var taskDTO = {
                    taskNum: task.taskNum,
                    planNum: null,
                    place: task.place,
                    memberId: planDTO.memberId,
                    dateN: task.dateN,
                    startTime: task.startTime,
                    duration: formatDurationToLocalTime(task.duration),  // 변환된 duration 사용
                    endTime: addMinutesToTime(task.startTime, task.duration),
                    task: task.task,
                    cost: task.cost || 0,
                    memo: null
                };

                // '이동' 작업일 경우, place를 다음 task의 place로 설정
                if (task.task == '이동' || task.task == '대중교통' || task.task == '도보') {
                    var nextTask = tasksForDate[index + 1];
                    if (nextTask && nextTask.place) {
                        taskDTO.place = nextTask.place;
                    } else {
                        // 다음 task가 없거나 place 정보가 없을 경우 처리
                        taskDTO.place = null;
                    }
                }

                allTasks.push(taskDTO);
            }
        }
    }
    $('#total-cost').html('예상 최소비용: ' + totalCost.toLocaleString() + '원');

    $('#save-button').click(function() {
        savePlan(allTasks);
    });

    // 시간 형식을 "HH:mm"으로 포맷팅하는 함수
    function formatTime(timeStr) {
        if (timeStr.includes(':')) {
            var parts = timeStr.split(':');
            var hours = parts[0].padStart(2, '0');
            var minutes = parts[1].padStart(2, '0');
            return hours + ':' + minutes;
        }
        return timeStr;
    }

    // Duration 문자열을 분으로 변환하는 함수
    function parseDuration(durationStr) {
        if (typeof durationStr === 'number') {
            return durationStr;
        } else if (durationStr.includes(':')) {
            var parts = durationStr.split(':');
            return parseInt(parts[0]) * 60 + parseInt(parts[1]);
        } else if (durationStr.includes('시간')) {
            var parts = durationStr.split('시간');
            var hours = parseInt(parts[0]);
            var mins = parseInt(parts[1].replace('분', '').trim());
            return hours * 60 + mins;
        } else if (durationStr.includes('분')) {
            return parseInt(durationStr.replace('분', '').trim());
        } else {
            return parseInt(durationStr);
        }
    }

    // 분을 "X시간 Y분" 형식의 문자열로 변환하는 함수
    function formatDuration(duration) {
        if (typeof duration === 'number') {
            var hours = Math.floor(duration / 60);
            var mins = duration % 60;
            return (hours > 0 ? hours + '시간 ' : '') + mins + '분';
        } else {
            return duration;
        }
    }

    // 시간에 분을 더하는 함수
    function addMinutesToTime(timeStr, minutesToAdd) {
        var parts = timeStr.split(':');
        var hours = parseInt(parts[0]);
        var minutes = parseInt(parts[1]) + minutesToAdd;

        while (minutes >= 60) {
            hours += 1;
            minutes -= 60;
        }

        if (hours >= 24) {
            hours -= 24;
        }

        var formattedHours = hours < 10 ? '0' + hours : hours;
        var formattedMinutes = minutes < 10 ? '0' + minutes : minutes;

        return formattedHours + ':' + formattedMinutes;
    }


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
                            payment: fastestRoute.info.payment,
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

    function savePlan(allTasks) {
        // PlanDTO 생성
        var newPlanDTO = {
            planNum: null, // 새로운 계획이므로 null 또는 서버에서 생성된 값
            planName: 'GPTのオススメプラン', // 필요에 따라 변경
            cmdNum: planDTO.cmdNum,
            memberId: planDTO.memberId, // 로그인된 사용자 ID로 대체
            startDate: planDTO.startDate, // 기존 planDTO의 startDate 사용
            endDate: planDTO.endDate,     // 기존 planDTO의 endDate 사용
            theme1: planDTO.theme1,       // 기존 planDTO의 theme1 사용
            theme2: planDTO.theme2,       // 기존 planDTO의 theme2 사용
            theme3: planDTO.theme3,       // 기존 planDTO의 theme3 사용
            planCreateDate: null, // 서버에서 설정
            planUpdateDate: null, // 서버에서 설정
            coverImageUrl: null, // 필요 시 설정
            taskList: allTasks   // 계산된 모든 TaskDTO 배열
        };

        // 서버로 전송
        sendPlanToServer(newPlanDTO);
    }

    function sendPlanToServer(planDTO) {
        $.ajax({
            url: '/saveGptPlan',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(planDTO),
            success: function(response) {
                alert('일정이 성공적으로 저장되었습니다.');
                window.location.href = "/";
            },
            error: function(error) {
                alert('일정 저장에 실패하였습니다.');
                console.error(error);
            }
        });
    }

    // duration을 HH:mm 형식으로 변환
    function formatDurationToLocalTime(duration) {
        var hours = Math.floor(duration / 60);
        var minutes = duration % 60;

        var formattedHours = hours < 10 ? '0' + hours : hours;
        var formattedMinutes = minutes < 10 ? '0' + minutes : minutes;

        return formattedHours + ':' + formattedMinutes;
    }

});

