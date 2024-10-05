/** 전역변수 **/
// 버튼번호 전역변수, 들어오자마자 0이고 그뒤로는 클릭한 버튼의 일자번호를 가져옴.
let dayNumOfButton = 0;

// 일정리스트(taskDTOList)
let taskListData = [];

// 일정리스트(placeInfoDTOList)
let placeListData = [];

// 일정마커 배열
let planAllMarkers = [];

// 일정마터 placeId 배열
let planPlaceIdArray = [];

// 테마마커 배열
let themeAllMarkers = [];

// 현재 선택한 테마
let previousTheme = '';
let currentTheme = '';

// 지도
let map = null;

// 지도 옵션(초기값 등)
let mapOptions = {};

// 테마클릭 번호(다른 테마 누르면 초기화 해야하므로, 전역)
let clickCountThemeButton = 0;

let clickCountEditTaskPlace = 0;
let clickCountAddNewTask = 0;

// 플랜번호
let planNum;

let targetTable;
let targetDayNum;
let targetTaskNum;
let targetTr;


let walkTime;
let transportTime;
let preTransDuration;
let nextTransDuration;
let preTransType;
let nextTransType;


// language 옵션 선택사항
let lang;
let themes = [];


$(document).ready(function () {
    planNum = $('#planNum').val();
    lang = $('#lang').val();

    $('.task-place-info-list-box').scroll(function () {
        let scrollTop = $(this).scrollTop();
    });

    // 일정표 출력함수
    dayPlansPrint(dayNumOfButton, planNum);

    // 일정마커 출력함수
    planMarkers(dayNumOfButton);

    //총비용 계산(ajax 로딩시간 주고, 계산)
    setTimeout(function () {
        calculateTotalCost(dayNumOfButton);
    }, 100);

    /************************** ajax로 그린 DOM요소에 이벤트 걸기 ************************/
    // 새로운 일정추가 위해 일정+ 버튼 클릭시
    $(document).on('click', '.day-table-add-new-task', function () {
        targetTable = $(this).closest('table');
        targetDayNum = $(this).data('table-num');
        clickCountAddNewTask++;

        // 테마버튼 애니메이션 효과 활성화
        clickUpdateTaskPlace();
    });

    // 수정버튼 클릭시, 수정가능요소 활성화
    $(document).on('click', '.editImgButton', activateDayTableInputs);

    // 일정표 플레이스 타이틀 클릭
    $(document).on('click', '.click-overlay-edit', function () {
        clickCountEditTaskPlace++;
        targetTr = $(this).closest('tr');
        targetTaskNum = $(this).data('task-num');
        clickUpdateTaskPlace();
    });

    $(document).on('mouseover', '.click-overlay-edit', function () {

    });

    // 인포리스트에서 일정추가버튼, theme-info-section에서 add-task-button 클릭시
    $(document).on('click', '.info-section-add-task-button', function () {

        // 기존에 있는 일정정보를 수정할 때
        if (clickCountEditTaskPlace === 1) {
            let indexTr = targetTr.index();
            let table = targetTr.closest('table');
            let lastTrIndex = table.find('tr').length - 3;

            let newPlaceId = $(this).data('place-id');
            let newX = $(this).data('map-x');
            let newY = $(this).data('map-y');

            // 첫번째 인덱스 요소일 때 : 이전 tr요소가 없다.
            if (indexTr === 0) {
                let nextX = targetTr.next().data('map-x');
                let nextY = targetTr.next().data('map-y');

                calNextTransDuration(newX, newY, nextX, nextY)
                    .then(() => {
                        console.log('Next duration calculated, updating task place...');
                        updateFirstTaskPlace(newPlaceId);
                    })
                    .catch((error) => {
                        console.error("Error occurred in sequence: ", error);
                    });
            }

            // 마지막인덱스 요소: nextTr 요소가 없다.
            if (indexTr === lastTrIndex) {
                let preX = targetTr.prev().data('map-x');
                let preY = targetTr.prev().data('map-y');
                calPreTransDuration(preX, preY, newX, newY)
                    .then(() => {
                        console.log('Next duration calculated, updating task place...');
                        updateLastTaskPlace(newPlaceId);
                    })
                    .catch((error) => {
                        console.error("Error occurred in sequence: ", error);
                    });
            }

            if (indexTr !== 0 && indexTr !== lastTrIndex) {
                let preX = targetTr.prev().data('map-x');
                let preY = targetTr.prev().data('map-y');

                let nextX = targetTr.next().data('map-x');
                let nextY = targetTr.next().data('map-y');

                calPreTransDuration(preX, preY, newX, newY)
                    .then(() => {
                        console.log('Pre duration calculated, moving to next duration...');
                        return calNextTransDuration(newX, newY, nextX, nextY);
                    })
                    .then(() => {
                        console.log('Next duration calculated, updating task place...');
                        updateTaskPlace(newPlaceId);
                    })
                    .catch((error) => {
                        console.error("Error occurred in sequence: ", error);
                    });
            }
        }

        if (clickCountAddNewTask === 1) {
            let newPlaceId = $(this).data('place-id');
            let newX = $(this).data('map-x');
            let newY = $(this).data('map-y');

            console.log(clickCountAddNewTask);

            // 마지막 일정의 장소정보 가져오기 --> 이동시간 계산용
            let currentLastTask = targetTable.find('tr:last-child .day-table-place-title');
            let lastTaskNum = currentLastTask.data('tasknum');
            let lastMapX = currentLastTask.data('map-x');
            let lastMapY = currentLastTask.data('map-y');

            calPreTransDuration(lastMapX, lastMapY, newX, newY)
                .then(() => {
                    console.log('Next duration calculated, updating task place...');
                    addNewTask(newPlaceId, lastTaskNum);
                })
                .catch((error) => {
                    console.error("Error occurred in sequence: ", error);
                });
        }
    });


    // 장소정보 더보기란에서 일정추가버튼, info-more-table 에서 add-task-button 클릭시
    $(document).on('click', '.info-table-add-task-button', function () {

        // 기존에 있는 일정정보를 수정할 때
        if (clickCountEditTaskPlace === 1) {
            let indexTr = targetTr.index();
            let table = targetTr.closest('table');
            let lastTrIndex = table.find('tr').length - 3;

            let newPlaceId = $(this).data('place-id');
            let newX = $(this).data('map-x');
            let newY = $(this).data('map-y');

            // 첫번째 인덱스 요소일 때 : 이전 tr요소가 없다.
            if (indexTr === 0) {
                let nextX = targetTr.next().data('map-x');
                let nextY = targetTr.next().data('map-y');

                calNextTransDuration(newX, newY, nextX, nextY)
                    .then(() => {
                        console.log('Next duration calculated, updating task place...');
                        updateFirstTaskPlace(newPlaceId);
                    })
                    .catch((error) => {
                        console.error("Error occurred in sequence: ", error);
                    });
            }

            // 마지막인덱스 요소: nextTr 요소가 없다.
            if (indexTr === lastTrIndex) {
                let preX = targetTr.prev().data('map-x');
                let preY = targetTr.prev().data('map-y');
                calPreTransDuration(preX, preY, newX, newY)
                    .then(() => {
                        console.log('Next duration calculated, updating task place...');
                        updateLastTaskPlace(newPlaceId);
                    })
                    .catch((error) => {
                        console.error("Error occurred in sequence: ", error);
                    });
            }

            if (indexTr !== 0 && indexTr !== lastTrIndex) {
                let preX = targetTr.prev().data('map-x');
                let preY = targetTr.prev().data('map-y');

                let nextX = targetTr.next().data('map-x');
                let nextY = targetTr.next().data('map-y');

                updatetaskplace(preX, preY, newX, newY)
                    .then(() => {
                        console.log('Pre duration calculated, moving to next duration...');
                        return calNextTransDuration(newX, newY, nextX, nextY);
                    })
                    .then(() => {
                        console.log('Next duration calculated, updating task place...');
                        updateTaskPlace(newPlaceId);
                    })
                    .catch((error) => {
                        console.error("Error occurred in sequence: ", error);
                    });
            }
        }

        if (clickCountAddNewTask === 1) {
            let newPlaceId = $(this).data('place-id');
            let newX = $(this).data('map-x');
            let newY = $(this).data('map-y');
            addNewTask(newPlaceId, newX, newY);
        }
    });

    // 확인버튼 클릭시 변경내용 저장
    $(document).on('click', '.saveImgButton', updateDurationCost);

    // 일정테이블에서 task-List 선택
    $(document).on('click', '.click-overlay', function () {
        let input = $(this).siblings('input');
        selectTaskList(input);
    });

    // 장소 상세정보 more 클릭시
    $(document).on('click', '.info-section-more', placeInfoMore);
    $(document).on('click', '.info-table-return-button', function () {
        $('.info-more-table-box').css('display', 'none');
        console.log(clickCountThemeButton);
        if (clickCountThemeButton === 0) {
            $('.task-place-info-list-box').css('display', 'block');
        }

        if (clickCountThemeButton === 1) {
            $('.theme-place-info-list-box').css('display', 'block');
        }

    });

    // 무장애, 문화유산, 펫 아이콘 후버시
    $(document).on('mouseover', '.info-table-badge', badgeExplain);

    // 무장애, 문화유산, 펫 아이콘 마우스 리브
    $(document).on('mouseleave', '.info-table-badge', badgeExplainHide);





    // 일자별 버튼 생성 및 데이 플랜 호출
    const days = ['allday', '1day', '2day', '3day', '4day', '5day', '6day'];
    days.forEach(function (day) {
        //const dayNum = day.replace('day', ''); // 숫자부분만 남긴다
        $('#plan-' + day + '-button').click(function () {
            console.log('데이버튼 클릭');

            days.forEach(function (otherDay) {
                $('#plan-' + otherDay + '-button').css('background-color', '#f0f0f0'); // Original color, adjust as needed
            });


            $(this).css('background-color', '#FAFFAF');
            $(this).css('border', 'solid 1px grey');
            // dayNum 저장
            dayNumOfButton = $(this).data('daynum-button');

            // 일자별 플랜 출력
            dayPlansPrint(dayNumOfButton);
            // 일자별 일정마커 출력
            planMarkers(dayNumOfButton);
            // 일자별 총비용 계산
            calculateTotalCost(dayNumOfButton);

            clickCountThemeButton = 0;

            // 테이블 번호 비교 --> 해당 일자만 프린트
            $('.planTables').each(function () {
                let dayNumOfTable = $(this).data('daynum-table');
                if (dayNumOfButton === 0) {
                    $('.planTables').css('display', 'block');
                } else if (dayNumOfButton === dayNumOfTable) {
                    $(this).css('display', 'block');
                } else {
                    $(this).css('display', 'none');
                }
            });
        });
    });


    // 테마별 버튼 생성 및 테마 정보 호출, 레포츠 처리 어떻게 해야 되지?
    if (lang === 'ko') {
        themes = ['쇼핑', '식당', '카페', '역사', '문화', '힐링', '랜드마크', '체험', '레포츠', '아이'];
    } else {
        themes = ['ショッピング', '食べ物', 'カフェ', '歴史', '文化', 'ヒーリング', 'ランドマーク', '体験', 'レジャー', '子供'];
    }
    themes.forEach(function (theme) {
        $('#theme-' + theme + '-button').click(function () {
            $('.theme-button').css('animation', 'none');

            if (clickCountAddNewTask === 1) {
                $('.notice-click-theme').css('display', 'none');
                $('.notice-click-add-button').css('display', 'block');
            }

            // 첫 클릭 때만, 여기서 currentTheme을 담음
            if (clickCountThemeButton === 0) {
                console.log('함수호출 전 클릭수: 0');
                currentTheme = $(this).data('theme');
            }

            if (clickCountThemeButton === 1) {
                previousTheme = currentTheme;
                currentTheme = $(this).data('theme');
                console.log('함수호출 전 클릭수: 1');
            }

            themeMarkers();

            console.log('함수 호출 후 & ajax전 currentTheme : ' + currentTheme);
            console.log('함수 호출 후 & ajax전 previousTheme : ' + previousTheme);

            themes.forEach(function (otherTheme) {
                $('#theme-' + otherTheme + '-button').css('background-color', '#f0f0f0'); // Original color, adjust as needed
            });

            if (previousTheme === currentTheme && clickCountThemeButton === 1) {
                $(this).css('background-color', '#f0f0f0');
            }
            if (previousTheme === '' || previousTheme !== currentTheme) {
                $(this).css('background-color', '#FAFFAF');
            }
        });
    });
});


// 일정표 그리기
function dayPlansPrint(dayNumOfButton, planNum) {

    $.ajax({
        url: '/myPage/myPlan/getPlan',
        type: 'post',
        data: {
            planNum: planNum,
            dayNumOfButton: dayNumOfButton
        },
        success: function (taskList) {

            $('#planTable-allDay').empty();

            let lastDay = taskList[taskList.length - 1].dateN;

            if (dayNumOfButton === 0) {
                dayNumOfButton++;
            }

            for (let dayNum = dayNumOfButton; dayNum <= lastDay; dayNum++) {

                let dayTable = `
                        <div id="planTable-${dayNum}day" class="planTables" data-daynum-table="${dayNum}">
                            <table class="day-plans-table">
                                <thead>
                                    <tr class="day-table-tr-basics">
                                        <th class="day-table-th-dayTitle" colspan="9">
                                            <h4 class="day-table-dayTitle">Day ${dayNum}</h4>
                                        </th>
                                        <th class="day-table-th-add-new-task" colspan="2">
                                            <img class="day-table-add-new-task" src="/images/customPlan/add-schedule.png" data-table-num="${dayNum}">
                                        </th>
                                    </tr>
                                    <tr class="day-table-tr1">
                                        <th class="day-table-th1">time</th>
                                        <th class="day-table-th2">task</th>
                                        <th class="day-table-th3">place</th>
                                        <th class="day-table-th4" colspan="4">duration</th>
                                        <th class="day-table-th5">cost</th>
                                        <th class="day-table-th6">edit</th>
                                    </tr>
                                </thead>
                                <tbody>`;

                taskList.forEach(function (task, index) {
                    if (task.dateN === dayNum) {

                        // 소요시간 변환
                        let durationHour = task.duration.substring(1, 2);
                        let durationMinute = task.duration.substring(3, 5);
                        let totalDurationInMinutes = Math.floor((durationHour * 60 + durationMinute) / 30);

                        // 타임라인 변환
                        let startTimeHour = task.startTime.substring(0, 2);
                        let startTimeMinute = task.startTime.substring(3, 5);
                        let startTime = startTimeHour + ':' + startTimeMinute;

                        let title = lang === 'ko' ? task.place.titleKr : task.place.titleJp;
                        let contentsType = lang === 'ko' ? task.contentsTypeKr : task.contentsTypeJp;
                        let contentsTypeMove = lang === 'ko' ? `이동(${task.contentsTypeKr})` : `移動(${task.contentsTypeJp})`;

                        // Start building task row
                        let rowHeight = totalDurationInMinutes * 0.2;
                        dayTable += `<tr class="task-list${task.taskNum}" data-tasknum="${task.taskNum}" data-map-x="${task.place.mapX}" data-map-y="${task.place.mapY}"  style="height:${rowHeight}px">                                        
                                        <td>${startTime}</td>`;

                        // 이동 task가 아닐 때
                        if (task.contentsTypeKr !== '도보' && task.contentsTypeKr !== '대중교통') {

                            dayTable += `
                                    <td>${contentsType}</td>
                                    <td class="day-table-td-place-title">
                                        <div class = "input-container">
                                            <input type="text" value="${title}" disabled data-tasknum="${task.taskNum}" data-place-id="${task.place.placeId}" data-map-x="${task.place.mapX}" data-map-y="${task.place.mapY}" class="day-table-place-title">
                                            <div class="click-overlay"></div>
                                            <div class="click-overlay-edit" data-task-num="${task.taskNum}" ></div>
                                        </div>    
                                    </td>`;

                        }
                        // 이동 task 일때
                        else {
                            dayTable += `
                                    <td colspan="2" class="day-table-contentTypeMove">${contentsTypeMove}</td>`;
                        }

                        // Add duration and cost columns
                        dayTable += `
                                <td class="day-table-td-duration"><input type="text" value="${durationHour}" disabled class="day-table-duration-hour"></td>
                                <td class="day-table-duration-h">h</td>
                                <td class="day-table-td-duration"><input type="text" value="${durationMinute}" disabled class="day-table-duration-minute"></td>
                                <td class="day-table-duration-minute">m</td>
                                <td class="day-table-td-cost"><input type="text" class="day-table-cost" data-daynum-cost="${task.dateN}" value="${task.cost}" disabled></td>
                                <td class="day-table-cost" >₩</td>`;

                        if (task.contentsTypeKr !== '도보' && task.contentsTypeKr !== '대중교통')
                            dayTable += `    
                                <td class="day-table-td-editImgButton">
                                    <img src="/images/customPlan/edit-circle.png" alt="Button Image" class="editImgButton" data-tasknum="${task.taskNum}">
                                    <img src="/images/customPlan/savebutton.png" alt="Button Image" class="saveImgButton" data-tasknum="${task.taskNum}">
                                </td>
                                <td class="day-table-td-deleteImgButton">
                                    <img src="/images/customPlan/delete-minus2.png" alt="Button Image" class="deleteImgButton">
                                </td>
                            </tr>`;
                    }
                });

                // Close the table structure
                dayTable += `</tbody></table></div>`;

                // Append the table for this day to the container

                $('#planTable-allDay').append(dayTable);
            }
        },
        error: function (e) {
            console.log(JSON.stringify(e));
        }
    });

}

/**
 * 새로운 일정 추가
 * @param newPlaceId
 * @param lastTaskNum
 */
function addNewTask(newPlaceId, lastTaskNum) {

    $.ajax({
        url: '/myPage/myPlan/addNewTask',
        type: 'post',
        data: {
            newPlaceId: newPlaceId
            , planNum: planNum
            , dayNum: targetDayNum
            , lastTaskNum: lastTaskNum
            , preTransDuration: preTransDuration
            , preTransType: preTransType
        },
        success: function () {
            console.log("왔다!");
            clickCountAddNewTask--;
            targetDayNum = '';
            preTransDuration = '';
            preTransType = '';
            dayPlansPrint(dayNumOfButton, planNum);
            $('.task-place-info-list-box').css('display', 'block');
            $('.theme-place-info-list-box').css('display', 'none');
        },
        error: function () {
        }
    })
}


/**
 * 일정장소 변경 위해, 일정테이블에서 장소명 클릭했을 때의 css효과, thememarkerList 비우기
 */
function clickUpdateTaskPlace() {
    $('.theme-button').css({
        'border': 'double 2px #FEFF9F',
        'border-radius': '20px',
        'animation': 'vibration 0.5s infinite',
        'background-color': '#f0f0f0'
    });

    $('.notice-click-theme').css('display', 'block');
    clearThemeMarkers();
    clickCountThemeButton = 0;
    previousTheme = '';
    currentTheme = '';
}


// 중간일정(첫번째, 마지막제외) 장소정보 수정
function updateTaskPlace(newPlaceId) {

    let preTaskNum = targetTr.prev().data('tasknum');
    let nextTaskNum = targetTr.next().data('tasknum');

    $.ajax({
        url: '/myPage/myPlan/updateTaskPlace',
        type: 'post',
        data: {
            planNum: planNum,
            targetTaskNum: targetTaskNum,
            newPlaceId: newPlaceId,
            preTaskNum: preTaskNum,
            preTransDuration: preTransDuration,
            preTransType: preTransType,
            nextTaskNum: nextTaskNum,
            nextTransDuration: nextTransDuration,
            nextTransType: nextTransType
        },
        success: function () {
            targetTaskNum = '';
            targetTr = '';
            walkTime = '';
            transportTime = '';
            preTransDuration = '';
            nextTransDuration = '';
            dayPlansPrint(dayNumOfButton, planNum);
            $('.task-place-info-list-box').css('display', 'block');
            $('.theme-place-info-list-box').css('display', 'none');
            clickCountEditTaskPlace--;
        },
        error: function (e) {
            console.log(JSON.stringify(e));
        }
    })

}

// 첫번째 이정 장소정보 수정
function updateFirstTaskPlace(newPlaceId) {
    let nextTaskNum = targetTr.next().data('tasknum');

    $.ajax({
        url: '/myPage/myPlan/updateFirstTaskPlace',
        type: 'post',
        data: {
            planNum: planNum,
            targetTaskNum: targetTaskNum,
            newPlaceId: newPlaceId,
            nextTaskNum: nextTaskNum,
            nextTransDuration: nextTransDuration
        },
        success: function () {
            targetTaskNum = '';
            targetTr = '';
            walkTime = '';
            transportTime = '';
            nextTransDuration = '';
            dayPlansPrint(dayNumOfButton, planNum);
            $('.task-place-info-list-box').css('display', 'block');
            $('.theme-place-info-list-box').css('display', 'none');
            clickCountEditTaskPlace--;
        },
        error: function (e) {
            console.log(JSON.stringify(e));
        }
    })

}


// 마지막 일정 장소정보 수정
function updateLastTaskPlace(newPlaceId) {

    let preTaskNum = targetTr.prev().data('tasknum');

    $.ajax({
        url: '/myPage/myPlan/updateLastTaskPlace',
        type: 'post',
        data: {
            planNum: planNum,
            targetTaskNum: targetTaskNum,
            newPlaceId: newPlaceId,
            preTaskNum: preTaskNum,
            preTransDuration: preTransDuration,
        },
        success: function () {
            targetTaskNum = '';
            targetTr = '';
            walkTime = '';
            transportTime = '';
            preTransDuration = '';
            nextTransDuration = '';
            dayPlansPrint(dayNumOfButton, planNum);
            $('.task-place-info-list-box').css('display', 'block');
            $('.theme-place-info-list-box').css('display', 'none');
            clickCountEditTaskPlace--;
        },
        error: function (e) {
            console.log(JSON.stringify(e));
        }
    })

}


// 수정버튼 클릭시, (1) input태그 활성화 (2) 수정버튼 숨기고, 체크버튼 활성화
function activateDayTableInputs() {
    // 해당 tr 특정
    let tr = $(this).closest('tr');

    // (1) input태그 활성화

    tr.css('background-color', 'transparent');
    tr.find('.day-table-duration-hour').prop('disabled', false);
    tr.find('.day-table-duration-minute').prop('disabled', false);
    tr.find('.day-table-cost').prop('disabled', false);

    tr.find('input').css('background-color', 'grey');

    tr.find('.click-overlay').css({
        'cursor': 'default',
        'z-index': '-1'
    });

    // (2)
    tr.find('.editImgButton').css('display', 'none');
    tr.find('.saveImgButton').css('display', 'block');
}


// 체크저장버튼 클릭시, 수정 메서드 (디비저장)
function updateDurationCost() {
    // validation function 필요 입력값이 정수인지, 정상적인 시간범위인지 duration - h는 최대 5, 분은 < 60 + 정수일것
    // number

    let tr = $(this).closest('tr');
    let newDurationHour = tr.find('.day-table-duration-hour').val();
    let newDurationMinute = tr.find('.day-table-duration-minute').val();
    let newCost = tr.find('td.day-table-td-cost input').val();
    let taskNum = $(this).data('tasknum');
    $('.theme-button').removeAttr('style');

    $.ajax({
        url: '/myPage/myPlan/updateDuration',
        type: 'post',
        data: {
            newDurationHour: newDurationHour,
            newDurationMinute: newDurationMinute,
            taskNum: taskNum,
            newCost: newCost,
            planNum: planNum
        },
        success: function () {
            dayPlansPrint(dayNumOfButton, planNum);

            setTimeout(function () {
                calculateTotalCost(dayNumOfButton);
            }, 100);

            $('.editImgButton').css('display', 'block');
            $('.saveImgButton').css('display', 'none');
        },
        error: function () {
            console.log('소요시간 업데이트 실패');
        }
    });
}

// 일정마커 출력 함수
function planMarkers(dayNumOfButton) {
    let planNum = $('#planNum').val();

    if (dayNumOfButton == null) {
        dayNumOfButton = 0;
    }
    clearPlanMarkers();
    clearThemeMarkers();

    $.ajax({
        url: '/myPage/myPlan/getPlan',
        type: 'post',
        data: {
            planNum: planNum,
            dayNumOfButton: dayNumOfButton
        },
        success: function (taskList) { // 해당 플랜에 해당하는 장소의 마커정보를 가져옴
            taskListData = taskList;

            mapOptions = {
                center: new naver.maps.LatLng(taskList[0].place.mapY, taskList[0].place.mapX),
                zoom: 14,
                miniZoom: 13,
                maxZoom: 20,
                zoomControl: true,
                zoomControlOptions: {
                    style: naver.maps.ZoomControlStyle.SMALL,
                    position: naver.maps.Position.TOP_RIGHT
                }
            };

            // 최초 로딩시 만들어지는 맵객체로 쭉간다.
            map = new naver.maps.Map(document.getElementById('map'), mapOptions);

            //List<TaskDTO>
            $.each(taskList, function (index, task) {
                let title = lang === 'ko' ? task.place.titleKr : task.place.titleJp;

                if (task.contentsTypeKr !== '도보' && task.contentsTypeKr !== '대중교통') {
                    const marker = new naver.maps.Marker({
                        map: map,
                        position: new naver.maps.LatLng(task.place.mapY, task.place.mapX),
                        title: title,
                        taskNum: task.taskNum,
                        placeId: task.place.placeId,
                        icon: {
                            content: `<svg xmlns="http://www.w3.org/2000/svg" height="50px" viewBox="0 -960 960 960" width="50px" fill="#96C9F4" stroke="#000000" stroke-width="10px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
                            anchor: new naver.maps.Point(11, 35)
                        },
                        zIndex: 100
                    });
                    planPlaceIdArray.push(marker.placeId);
                    planAllMarkers.push(marker); // 생성된 마커 배열에 저장
                }
            }); // 반복문 끝


            // 전역으로 선언된 map 객체를 이용해 이벤트 리스너 추가,  (맵 이동시 자동으로 호출되는 idle)
            naver.maps.Event.addListener(map, 'idle', function () {
                idleEvent(map, planAllMarkers, themeAllMarkers); // Update markers after map movement
            });

            naver.maps.Event.trigger(map, 'idle');

            // 줌레벨에 따른 테마마커 모양 변화
            naver.maps.Event.addListener(map, 'zoom_changed', function () {
                let currentZoom = map.getZoom();
                console.log('줌레벨');
                // Conditional logic based on zoom level
                $.each(themeAllMarkers, function (index, themeMarker) {
                    if (currentZoom > 15) {
                        // Use a larger circle for higher zoom levels
                        themeMarker.setIcon({
                            content: `<svg xmlns="http://www.w3.org/2000/svg" height="40px" viewBox="0 -960 960 960" width="35px" fill="#092a2a" stroke="white" stroke-width="10px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
                            anchor: new naver.maps.Point(15, 15) // Adjust anchor accordingly
                        });
                    } else if (currentZoom > 8) {
                        // Medium-sized circle for mid zoom levels
                        themeMarker.setIcon({
                            content: `<svg xmlns="http://www.w3.org/2000/svg" width="20px" height="20px"
                                viewBox="0 0 20 20" fill="#092a2a" stroke="white" stroke-width="1px" stroke-opacity="0.24">
                                <circle cx="10" cy="10" r="6"/></svg>`,
                            anchor: new naver.maps.Point(10, 10) // Adjust anchor accordingly
                        });
                    } else {
                        // Small circle for lower zoom levels
                        themeMarker.setIcon({
                            content: `<svg xmlns="http://www.w3.org/2000/svg" width="10px" height="10px"
                                viewBox="0 0 10 10" fill="#006400" stroke="white" stroke-width="1px" stroke-opacity="0.24">
                                <circle cx="5" cy="5" r="4"/></svg>`,
                            anchor: new naver.maps.Point(5, 5) // Adjust anchor accordingly
                        });
                    }
                });
            });

            // 일정마커 클릭이벤트 설정 : 여기에 달아둬야, 아래에서 호출가능
            for (const marker of planAllMarkers) {
                naver.maps.Event.addListener(marker, 'click', function () { // 뭐야 표시는 이렇게 되는데 정상적으로 작동하잖아
                    const planMarkerKey = marker.taskNum; // planMrker는 title: 제목명, taskNum, placeId 저장했음, 아무튼 플랜마커의 마커키는 taskNum
                    const planMarkerPlaceId = marker.placeId;
                    selectPlanMarker(planMarkerKey, planMarkerPlaceId);

                    $('.place-info-modal').css('display', 'none');
                });
            }
            ;
        },
        error: function (error) {
            console.error('에러발생:', error);
            alert('전체 일정 데이터 전송 실패');
        }
    });
}

// placeInfoList 에서 More을 클릭하면 출력되는 상세페이지
function placeInfoMore() {
    let placeId = $(this).data('place-id');
    let taskNum = $(this).data('task-num');

    $.ajax({
        url: '/myPage/myPlan/placeInfoMore',
        type: 'post',
        data: {placeId: placeId},
        success: function (placeDTO) {
            let heritageIcon = placeDTO.heritage ? `<img th:src="@{/images/customPlan/heritage.png}" class="info-table-badge" data-badge="heritage">` : '';
            let barrierFreeIcon = placeDTO.barrierFree ? '<img src="/images/customPlan/barrier.png" class="info-table-badge" data-badge="barrier">' : '';
            let petFriendlyIcon = placeDTO.petFriendly ? '<img src="/images/customPlan/pet.png" class="info-table-badge" data-badge="pet">' : '';
            let feeInfo = '';
            let saleItem = '';
            let usetime = '';
            let restdate = '';
            let overview = '';

            if (placeDTO.feeInfoKr !== null && placeDTO.feeInfoKr !== undefined) {
                feeInfo = lang === 'ko' ? placeDTO.feeInfoKr : placeDTO.feeInfoJp;
            }

            if (placeDTO.saleItemKr !== null && placeDTO.saleItemKr !== undefined) {
                saleItem = lang === 'ko' ? placeDTO.saleItemKr : placeDTO.saleItemJp;
            }

            if (placeDTO.usetimeKr !== null && placeDTO.usetimeKr !== undefined) {
                usetime = lang === 'ko' ? placeDTO.usetimeKr : placeDTO.usetimeJp;
            }

            if (placeDTO.restdateKr !== null && placeDTO.restdateKr !== undefined) {
                restdate = lang === 'ko' ? placeDTO.restdateKr : placeDTO.restdateJp;
            }

            if (placeDTO.overviewKr !== null && placeDTO.overviewKr !== undefined) {
                overview = lang === 'ko' ? placeDTO.overviewKr : placeDTO.overviewJp;
            }

            let title = lang === 'ko' ? placeDTO.titleKr : placeDTO.titleJp;

            let addButton = taskNum === undefined ? '<img src="/images/customPlan/add-point.png" class="info-table-add-task-button" data-place-id="${placeDTO.placeId}" data-map-x="${placeDTO.mapX}" data-map-y="${placeDTO.mapY}">' : '';

            let infoMoreTable = `
                         <div class="info-more-table-box">
                            <table class="info-table">
                                <thead>     
                                    <tr class="info-table-basic">
                                        <th class="info-table-title" colspan="2">
                                            <h4 class="in-title">${title}</h4>
                                            <span>${heritageIcon}</span>
                                            <span>${barrierFreeIcon}</span>
                                            <span>${petFriendlyIcon}</span>
                                        </th>
                                        <td class="info-table-back-td">   
                                            ${addButton}                        
                                            <img src="/images/customPlan/return2.png" class="info-table-return-button">                        
                                        </td>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>    
                                        <th class="info-table-th"><img src="/images/customPlan/clock.png" class="info-table-images"></th>
                                        <td class="info-table-usetime">${usetime}</td>
                                    </tr>
                                    <tr>    
                                        <th class="info-table-th"><img src="/images/customPlan/restday.png" class="info-table-images"></th>
                                        <td class="info-table-restday">${restdate}</td>
                                    </tr>
                                    <tr>    
                                        <th class="info-table-th"><img src="/images/customPlan/fee-Info-won.png" class="info-table-images"></th>
                                        <td class="info-table-fee-info">${feeInfo}</td>
                                    </tr>
                                    <tr>    
                                        <th class="info-table-th"><img src="/images/customPlan/thumbup.png" class="info-table-images"></th>
                                        <td class="info-table-sale-item">${saleItem}</td>
                                    </tr>
                                    <tr>
                                        <th class="info-table-th"><img src="/images/customPlan/description.png" class="info-table-images"></th>
                                        <td class="info-table-description" rowspan="2">${overview}</td>
                                    </tr>
                                        <th class="info-table-th"></th>
                                </tbody>    
                            </table>
                        </div>`

            $('.place-info-modal').html(infoMoreTable);

            // 플랜마커 장소정보 리스트박스 off
            $('.task-place-info-list-box').css('display', 'none');

            // 테마마커 장소정보 리스트박스 off
            $('.theme-place-info-list-box').css('display', 'none');

            // 모달창 on
            $('.place-info-modal').css('display', 'block');
        }
    })
}

// taskList 클릭시(마커 색 변경, 일정표 해당 tr 색 변경), input은 place.title input태그
function selectTaskList(input) {
    let tr = input.closest('tr');
    let clickedPlaceId = input.data('place-id'); // 클릭한 tr의 data-tasknum 가져오기
    let clickedTaskNum = input.data('tasknum');

    // (1) task-list tr 배경색상 설정
    $('[class^="task-list"]').removeClass('selected');
    tr.addClass('selected');

    console.log(clickedPlaceId);
    // (2) place-info-list 배경색상 설정
    $('[class^="place-info-section"]').removeClass('selected');
    $('.place-info-section' + clickedPlaceId).addClass('selected');

    // (4) 마커색상 설정
    planAllMarkers.forEach(function (marker) {
        if (marker.taskNum === clickedTaskNum) {
            // 클릭한 taskNum과 일치하는 마커에 대해 아이콘 변경
            marker.setIcon({
                content: `<svg xmlns="http://www.w3.org/2000/svg" height="60px" viewBox="0 -960 960 960" width="60px" fill="#0F67B1" stroke="#000000" stroke-width="10px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
                anchor: new naver.maps.Point(11, 35)
            });
            marker.setZIndex(100); // 선택된 마커를 위에 표시
        } else {
            // 선택되지 않은 마커에 대해 아이콘 변경
            marker.setIcon({
                content: `<svg xmlns="http://www.w3.org/2000/svg" height="50px" viewBox="0 -960 960 960" width="50px" fill="#96C9F4" stroke="#000000" stroke-width="10px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
                anchor: new naver.maps.Point(11, 35)
            });
            marker.setZIndex(0); // 선택되지 않은 마커를 기본 zIndex로 설정
        }

    });
    // 클릭한 마커로 지도 중심 이동 및 줌 레벨 조정
    let targetMarker = planAllMarkers.find(function (marker) {
        return marker.taskNum === clickedTaskNum;
    });

    if (targetMarker) {
        let position = targetMarker.getPosition(); // 마커의 위치 가져오기
        map.setCenter(position); // 지도의 중심을 해당 마커 위치로 이동
        map.setZoom(16); // 줌 레벨 조정 (필요에 따라)

    }

}


// 일정마커 클릭시, planMarkerKey = taskNum
function selectPlanMarker(markerKey, planMarkerPlaceId) {
    // 상세정보 후 return버튼, planmarkerinfolist 제대로 출력하기 위해 --
    clickCountThemeButton--;

    // 장소정보 리스트박스 on
    $('.task-place-info-list-box').css('display', 'block');

    // 모달창 off
    $('.place-info-modal').css('display', 'none');

    // (1) task-list tr 배경색상 설정
    $('[class^="task-list"]').removeClass('selected');
    $('.task-list' + markerKey).closest('tr').addClass('selected');

    // (2) place-info-list 배경색상 설정
    $('[class^="place-info-section"]').removeClass('selected');
    $('.place-info-section' + planMarkerPlaceId).addClass('selected');

    // (3) 일정마커 색상 설정
    planAllMarkers.forEach(marker => {
        if (marker.taskNum === markerKey) {
            // 선택된 마커 강조
            marker.setIcon({
                content: `<svg xmlns="http://www.w3.org/2000/svg" height="60px" viewBox="0 -960 960 960" width="60px" fill="#0F67B1" stroke="#000000" stroke-width="10px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
                size: new naver.maps.Size(22, 35),
                anchor: new naver.maps.Point(11, 35)
            });
            marker.setZIndex(100);
        } else {
            // 나머지 일정마커 색상 초기화
            marker.setIcon({
                content: `<svg xmlns="http://www.w3.org/2000/svg" height="50px" viewBox="0 -960 960 960" width="50px" fill="#96C9F4" stroke="#000000" stroke-width="10px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
                size: new naver.maps.Size(22, 35),
                anchor: new naver.maps.Point(11, 35)
            });
            marker.setZIndex(0);
        }
    });

    // 테마마커 색상 초기화
    themeAllMarkers.forEach(marker => {
        marker.setIcon({
            content: `<svg xmlns="http://www.w3.org/2000/svg" width="20px" height="20px"
                                                    viewBox="0 0 20 20" fill="#002379" stroke="#00000 stroke-width="0.3px" stroke-opacity="0.24">
                                                    <circle cx="8" cy="8" r="6"/></svg>`,
            size: new naver.maps.Size(13, 13),
            anchor: new naver.maps.Point(11, 35)
        });
    })
}


// 테마마커 출력
function themeMarkers() {
    console.log('함수호출 후 currentTheme:' + currentTheme);
    console.log('함수호출 후 previousTheme:' + previousTheme);
    clearThemeMarkers();

    if (clickCountThemeButton === 1) {
        console.log('현재 클릭수 : 1');
        clickCountThemeButton--;
        //previousTheme = currentTheme;
    }

    if (clickCountThemeButton === 0) {
        console.log('현재 클릭수 : 0');
    }
    console.log('ajax 전 currentTheme:' + currentTheme);
    console.log('ajax 전 previousTheme:' + previousTheme);

    // 테마버튼 클릭 기본 설정 : theme 출력, task 막음
    $('.theme-place-info-list-box').css('display', 'block');
    $('.task-place-info-list-box').css('display', 'none');

    // 테마버튼 누른상태여서 1이라면, 비우고 0으로 만든다.
    // if (clickCountThemeButton === 1) {
    //     clearThemeMarkers();
    //     clickCountThemeButton--;
    // }

    // 같은테마 클릭했을 시 : theme 막고, task 출력, 테마배열비움
    if (previousTheme === currentTheme) {
        $('.theme-place-info-list-box').css('display', 'none');
        $('.task-place-info-list-box').css('display', 'block');
    }

    console.log('ajax 실행전 clickCount :' + clickCountThemeButton);
    // 다른 테마를 눌렀을 때만, 다시 출력
    if (previousTheme !== currentTheme) {
        $.ajax({
            url: '/myPage/myPlan/themeMarkers',
            type: 'post',
            data: {
                theme: currentTheme
                , planNum: planNum
                , dayNumOfButton: dayNumOfButton
            },
            success: function (placeListByTheme) {

                // idleEvent --> updateThemeInfoList 위해, palceListData 전역변수에 데이터담음.
                placeListData = placeListByTheme;

                // 여기까지 오면 일단 +1
                clickCountThemeButton++;

                // 테마 마커배열 채우기 & 상세정보
                $.each(placeListByTheme, function (index, placeByTheme) {
                    // 플랜마커에 포함되어 있는 건 테마 마커로 표시하지 않음
                    if (!planPlaceIdArray.includes(placeByTheme.placeId)) {
                        let title = lang === 'ko' ? placeByTheme.titleKr : placeByTheme.titleJp;
                        let address = lang === 'ko' ? placeByTheme.addressKr : placeByTheme.addressJp;

                        let marker = new naver.maps.Marker({
                            map: map,
                            position: new naver.maps.LatLng(placeByTheme.mapY, placeByTheme.mapX),
                            title: title,
                            placeId: placeByTheme.placeId,
                            icon: {
                                content: `<svg xmlns="http://www.w3.org/2000/svg" width="16px" height="16px"
                                                    viewBox="0 0 16 16" fill="#092a2a" stroke="white" stroke-width="5px" stroke-opacity="0.24">
                                                    <circle cx="8" cy="8" r="4"/></svg>`,
                                anchor: new naver.maps.Point(13, 13)
                            },
                            zIndex: 100
                        });

                        let contents = [
                            '<div class="marker-place-info">',
                            `<img src="${placeByTheme.originImgUrl}">`,
                            `   <h4>${title}</h4>`,
                            `   <p>${address}<br />`,
                            '       <br />',
                            '   </p>',
                            '</div>'
                        ].join('')
                        marker.placeId = placeByTheme.placeId;
                        marker.contents = contents;
                        themeAllMarkers.push(marker);
                    }
                }); // 테마정보 반복문 끝

                console.log('ajax 실행 후 clickCount :' + clickCountThemeButton);

                // 모든 테마마커를 순회하며 클릭 이벤트 --> 색상, 저장된 상세내용 프린트
                for (const marker of themeAllMarkers) {
                    naver.maps.Event.addListener(marker, 'click', function () { // 뭐야 표시는 이렇게 되는데 정상적으로 작동하잖아
                        // 마커는 title이라는 값을 관광지 데이터는 객체의 key 값으로 관리하였기 때문에 마커 클릭과 관광지 클릭이라는 별개의 이벤트에 대해 동일한 함수를 사용할 수 있음 (db의 외래키 개념 차용, 실제 프로젝트에서는 관광지 데이터의 primary 키를 객체의 key값으로 사용)
                        const markerKey = marker.placeId;
                        selectThemeMarker(markerKey);

                        $('.placeInfo').html(marker.contents);
                    });
                }
                ;

                naver.maps.Event.trigger(map, 'idle');
                console.log('ajax 후 currentTheme:' + currentTheme);
                console.log('ajax 후 previousTheme:' + previousTheme);

            },
            error: function (error) {
                console.error('에러발생:', error);
                alert('테마정보 로드 실패');
            }
        }); // ajax문
    } // previousTheme !== currentTheme if문
}


// 테마마커 선택시
function selectThemeMarker(markerKey) {

    if (clickCountThemeButton === 0) {
        clickCountThemeButton++;
    }
    console.log(markerKey); //
    // 마커 목록 순회
    themeAllMarkers.forEach(marker => {
        if (marker.placeId === markerKey) {
            // 선택된 마커 강조
            marker.setIcon({
                content: `<svg xmlns="http://www.w3.org/2000/svg" height="35px" viewBox="0 -960 960 960" width="35px" fill="#092a2a" stroke="white" stroke-width="5px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
                size: new naver.maps.Size(22, 35),
                anchor: new naver.maps.Point(11, 35)
            });
        } else {
            // 나머지 마커는 기본 색상으로 초기화
            marker.setIcon({
                content: `<svg xmlns="http://www.w3.org/2000/svg" width="16px" height="16px"
                                                    viewBox="0 0 16 16" fill="#092a2a" stroke=white stroke-width="3px" stroke-opacity="0.24">
                                                    <circle cx="8" cy="8" r="4"/></svg>`,
                size: new naver.maps.Size(22, 35),
                anchor: new naver.maps.Point(13, 13)
            });
        }
    });
    planAllMarkers.forEach(marker => {
        marker.setIcon({
            content: `<svg xmlns="http://www.w3.org/2000/svg" height="50px" viewBox="0 -960 960 960" width="50px" fill="#96C9F4"  stroke="#000000" stroke-width="10px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
            size: new naver.maps.Size(22, 35),
            anchor: new naver.maps.Point(11, 35)
        });
    })
}

// idleEvent 지도 이동시 실행되는 함수
function idleEvent(map, planAllMarkers, themeAllMarkers) {

    $('.place-info-modal').css('display', 'none');

    if (clickCountThemeButton === 0) {
        $('.task-place-info-list-box').css('display', 'block');
        $('.theme-place-info-list-box').css('display', 'none');
    }

    if (clickCountThemeButton === 1) {
        $('.task-place-info-list-box').css('display', 'none');
        $('.theme-place-info-list-box').css('display', 'block');
    }


    const mapBounds = map.getBounds();
    const visiblePlanMarkerKeyList = [];
    const visibleThemeMarkerKeyList = [];


    // Check which plan markers are within the map bounds
    for (const planMarker of planAllMarkers) {
        const position = planMarker.getPosition();
        if (mapBounds.hasLatLng(position)) {
            visiblePlanMarkerKeyList.push(planMarker.taskNum);  // Push placeId for identification
        }
    }

    // Check which theme markers are within the map bounds
    for (const themeMarker of themeAllMarkers) {
        const position = themeMarker.getPosition();
        if (mapBounds.hasLatLng(position)) {
            visibleThemeMarkerKeyList.push(themeMarker.placeId); // Push placeId for identification
        }
    }

    // Update the list of visible markers' info
    updatePlanInfoList(visiblePlanMarkerKeyList);

    if (themeAllMarkers.size !== 0) {
        updateThemeInfoList(visibleThemeMarkerKeyList);
    }
}

// 일정마커 장소정보List 출력함수
function updatePlanInfoList(visiblePlanMarkerKeyList) {
    const infoList = document.querySelector('.task-place-info-list-box');

    // Clear existing list
    while (infoList.firstChild) {
        infoList.removeChild(infoList.firstChild);
    }

    // Loop through visible marker keys
    for (const key of visiblePlanMarkerKeyList) {
        const task = taskListData.find(t => t.taskNum === key); // Assuming taskList is globally accessible
        // Create the HTML structure for each visible marker
        let img = task.place.originImgUrl === null ?
            '<img src="/images/customPlan/nonImg.png" class="place-info-imgNone">' :
            `<img src="${task.place.originImgUrl}" class="place-info-img">`;

        let title = lang === 'ko' ? task.place.titleKr : task.place.titleJp;
        let address = lang === 'ko' ? task.place.addressKr : task.place.addressJp;
        let contentsType = lang === 'ko' ? task.place.contentsTypeKr : task.place.contentsTypeJp;

        infoList.innerHTML += `
            <div class="place-info-section${task.place.placeId}" >
                ${img}
                <h5 class="place-info-title">${title}</h5>
                <img src="/images/customPlan/more2.png" class="info-section-more" data-place-id="${task.place.placeId}" data-task-num="${task.taskNum}">
                <span class="info-section-address">${address}</span>
                <span class="info-section-contentsType">${contentsType}</span>
                <img src="/images/customPlan/infocenter.png" class="info-section-infocenterImg">
                <span class="info-section-infocenter">${task.place.infocenter}</span>
            </div>
        `;
    }
}

// 테마마커 장소정보List 출력함수, info-section
function updateThemeInfoList(visibleThemeMarkerKeyList) {
    const infoList = document.querySelector('.theme-place-info-list-box');

    // console.log(visibleThemeMarkerKeyList);
    // Clear existing list
    while (infoList.firstChild) {
        infoList.removeChild(infoList.firstChild);
    }

    // Loop through visible marker keys
    for (const key of visibleThemeMarkerKeyList) {
        const place = placeListData.find(p => p.placeId === key); // Assuming taskList is globally accessible
        // Create the HTML structure for each visible marker
        let img = place.originImgUrl === null ?
            '<img src="/images/customPlan/nonImg.png" class="place-info-imgNone">' :
            `<img src="${place.originImgUrl}" class="place-info-img">`;

        let title = lang === 'ko' ? place.titleKr : place.titleJp;
        let address = lang === 'ko' ? place.addressKr : place.addressJp;
        let contentsType = lang === 'ko' ? place.contentsTypeKr : place.contentsTypeJp;

        infoList.innerHTML += `
            <div class="place-info-section${place.placeId}" >
                ${img}
                <h5 class="place-info-title">${title}</h5>
                <img src="/images/customPlan/add-point.png" class="info-section-add-task-button" data-place-id="${place.placeId}" data-map-x="${place.mapX}" data-map-y="${place.mapY}">
                <img src="/images/customPlan/more2.png" class="info-section-more" data-place-id="${place.placeId}"></img>
                <span class="info-section-address">${address}</span>
                <span class="info-section-contentsType">${contentsType}</span>
                <img src="/images/customPlan/infocenter.png" class="info-section-infocenterImg">
                <span class="info-section-infocenter">${place.infocenter}</span>   
           </div>
        `;
    }
}

// 업로드 최적화 위한 함수 - 보이는 지도에서만 마커 뜨게
function updateMarkers(map, themeAllMarkers, planAllMarkers) {
    var mapBounds = map.getBounds();
    var marker, position;

    for (var i = 0; i < themeAllMarkers.length; i++) {
        marker = themeAllMarkers[i];

        if (mapBounds.hasLatLng(position)) {
            showMarker(map, marker);
        } else {
            hideMarker(map, marker);
        }
    } // themeAllMarkers 리스트 체크

    for (var i = 0; i < planAllMarkers.length; i++) {
        marker = planAllMarkers[i];

        if (mapBounds.hasLatLng(position)) {
            showMarker(map, marker);
        } else {
            hideMarker(map, marker);
        }
    } // planAllMarkers 리스트 체크
}

// 범위 내 마커 보이게
function showMarker(map, marker) {
    if (marker.getMap()) return;
    marker.setMap(map);
}

// 범위 밖 마커 안보이게
function hideMarker(map, marker) {
    if (!marker.getMap()) return;
    marker.setMap(null);
}

// 총비용 계산(allDay, 일자별 계산 모두 가능)
function calculateTotalCost(dayNumOfButton) {
    let totalCost = 0;
    $('.day-table-td-cost input').each(function () {
        let dayNumOfCost = $(this).data('daynum-cost');

        // 0일때 즉; 처음 로딩하거나, all-day이일때
        if (dayNumOfButton === 0) {
            // 모든 cost
            let cost = parseInt($(this).val());
            if (!isNaN(cost)) {
                totalCost += cost;
            }
        } else {
            //데이값이 일치하는 cost만
            if (dayNumOfButton === dayNumOfCost) {
                let cost = parseInt($(this).val());
                if (!isNaN(cost)) {
                    totalCost += cost;
                }
            }
        }
    });
    $('#totalCost').text(totalCost.toLocaleString() + ' 원'); // 총합을 표시할 요소에 추가
}

// 일정마커 배열을 비우기
function clearPlanMarkers() {
    $.each(planAllMarkers, function (index, marker) {
        marker.setMap(null); // 마커를 지도에서 제거
    });
    planAllMarkers.length = 0; // 배열 비우기
}

// 테마마커 배열을 비우기
function clearThemeMarkers() {
    $.each(themeAllMarkers, function (index, marker) {
        marker.setMap(null); // 마커를 지도에서 제거
    });
    themeAllMarkers.length = 0; // 배열 비우기
}

// 배지 mouseover 효과, 상세설명 출력
function badgeExplain() {
    let badge = $(this).data('badge');
    console.log(badge);
    $('#' + badge + 'Badge-explain').css({
        display: 'block'
    })
}

// 배지 mouseleave 효과, 상세설명 숨기기
function badgeExplainHide() {
    let badge = $(this).data('badge');
    $('#' + badge + 'Badge-explain').css('display', 'none');
}

function searchPubTransPathAJAX(sx, sy, ex, ey) {
    return new Promise((resolve, reject) => {
        var xhr = new XMLHttpRequest();
        var url = "https://api.odsay.com/v1/api/searchPubTransPathT?SX=" + sx + "&SY=" + sy +
            "&EX=" + ex + "&EY=" + ey + "&apiKey=AbfWDSywAKWcKBRv/ClpFQ";

        xhr.open("GET", url, true);
        xhr.send();

        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    var responseText = xhr.responseText;
                    var data = JSON.parse(responseText);

                    if (data.result && data.result.path) {
                        // 가장 빠른 경로 찾기
                        const fastestRoute = data.result.path.reduce((min, current) => {
                            return current.info.totalTime < min.info.totalTime ? current : min;
                        });
                        transportTime = fastestRoute.info.totalTime;
                        resolve(transportTime); // Promise resolve
                    } else {
                        transportTime = 10000;
                        resolve(transportTime); // 기본값
                    }
                } else {
                    reject(new Error("Request failed with status: " + xhr.status));
                }
            }
        };
    });
}

function Tmap(sx, sy, ex, ey) {
    var headers = {};
    headers["appKey"] = "7ejrjQSxsM8Vp5U8WbLArOuHpOwQNnJ31hqE3Pt7";

    return new Promise((resolve, reject) => {
        $.ajax({
            url: "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&format=json&callback=result",
            method: "POST",
            headers: headers,
            contentType: 'application/json',
            data: JSON.stringify({
                "startX": sx,
                "startY": sy,
                "endX": ex,
                "endY": ey,
                "reqCoordType": "WGS84GEO",
                "resCoordType": "EPSG3857",
                "startName": "출발지",
                "endName": "도착지"
            }),
            success: function (response) {
                var resultData = response.features;
                walkTime = (resultData[0].properties.totalTime) / 60; //
                resolve(walkTime); // Promise resolve
            },
            error: function (xhr, status, error) {
                reject(new Error("Error in Tmap: " + status));
            }
        });
    });
}

function calPreTransDuration(preX, preY, newX, newY) {
    return Promise.all([
        searchPubTransPathAJAX(preX, preY, newX, newY),
        Tmap(preX, preY, newX, newY),
    ]).then(([transportTime, walkTime]) => {
        console.log("pre 대중교통:", transportTime);
        console.log("Pre 도보:", walkTime);
        preTransDuration = transportTime > walkTime ? walkTime : transportTime;
        preTransType = transportTime > walkTime ? '도보' : '대중교통';
        console.log("앞수단:", preTransType);
        console.log("앞이동시간:", preTransDuration);
        return preTransDuration; // Return preTransDuration
    }).catch(error => {
        console.error("Error in updatetaskplace:", error);
        throw error; // Rethrow to propagate error
    });
}


function calNextTransDuration(newX, newY, nextX, nextY) {
    return Promise.all([
        searchPubTransPathAJAX(newX, newY, nextX, nextY),
        Tmap(newX, newY, nextX, nextY)
    ]).then(([transportTime, walkTime]) => {
        console.log("next 대중교통:", transportTime);
        console.log("next 도보:", walkTime);
        nextTransDuration = transportTime > walkTime ? walkTime : transportTime;
        nextTransType = transportTime > walkTime ? '도보' : '대중교통';
        console.log("뒤수단:", nextTransType);
        console.log("뒤이동시간:", nextTransDuration);
        return nextTransDuration;
    });

}


