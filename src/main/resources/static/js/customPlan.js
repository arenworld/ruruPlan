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

// 플랜번호
let planNum;

$(document).ready(function () {
    planNum = $('#planNum').val();

    // 일정표 출력함수
    dayPlansPrint(dayNumOfButton, planNum);

    // 일정마커 출력함수
    planMarkers(dayNumOfButton);

    //총비용 계산(ajax 로딩시간 주고, 계산)
    setTimeout(function () {
        calculateTotalCost(dayNumOfButton);
    }, 100);

    /** ajax로 그린 DOM요소에 이벤트 걸기 **/
    // 수정버튼 클릭시, 수정가능요소 활성화
    $(document).on('click', '.editImgButton', activateDayTableInputs);

    // 일정표 플레이스 타이틀 클릭
    $(document).on('click', '.click-overlay-edit', updateTaskPlace);
    $(document).on('mouseover', '.click-overlay-edit', function () {

    });

    // 확인버튼 클릭시 변경내용 저장
    $(document).on('click', '.saveImgButton', updateDurationCost);

    // task-List 선택
    $(document).on('click', '.click-overlay', function () {
        let input = $(this).siblings('input');
        console.log('Clicked overlay, related input:', input);
        selectTaskList(input);
    });

    // 장소 상세정보 more 클릭시
    $(document).on('click', '.place-info-more', placeInfoMore);

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


            $(this).css('background-color', 'pink');
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
    const themes = ['쇼핑', '식당', '카페', '역사', '문화', '힐링', '랜드마크', '체험', '레포츠']
    themes.forEach(function (theme) {
        $('#theme-' + theme + '-button').click(function () {

            $('.theme-button').css('animation', 'none');

            // 첫 클릭 때만, 여기서 currentTheme을 담음
            if(clickCountThemeButton === 0) {
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
                $(this).css('background-color', 'pink');
            }


        });
    });

});


// 일정표 그리기
function dayPlansPrint(dayNumOfButton, planNum) {

    $.ajax({
        url: '/custom/getPlan',
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
                            <h4 class="day-plans-dayTitle">Day ${dayNum}</h4>
                            <table class="day-plans-table">
                                <thead>
                                    <tr class="day-table-tr1">
                                        <th class="day-table-th2">time</th>
                                        <th class="day-table-th3">task</th>
                                        <th class="day-table-th4">place</th>
                                        <th class="day-table-th5" colspan="4">duration</th>
                                        <th class="day-table-th6">cost</th>
                                        <th class="day-table-th7">edit</th>
                                    </tr>
                                </thead>
                                <tbody>`;

                taskList.forEach(function (task, index) {
                    if (task.dateN === dayNum) {

                        // 소요시간 변환
                        let durationHour = task.duration.substring(1, 2);
                        let durationMinute = task.duration.substring(3, 5);

                        // 타임라인 변환
                        let startTimeHour = task.startTime.substring(0, 2);
                        let startTimeMinute = task.startTime.substring(3, 5);
                        let startTime = startTimeHour + ':' + startTimeMinute;


                        // Start building task row
                        dayTable += `<tr class="task-list${task.taskNum}" data-tasknum="${task.taskNum}">                                        
                                        <td>${startTime}</td>`;

                        // 이동 task가 아닐 때
                        if (task.task !== '이동') {
                            dayTable += `
                                    <td>${task.task}</td>
                                    <td class="day-table-td-place-title">
                                    <div class = "input-container">
                                        <input type="text" value="${task.place.titleKr}" disabled data-tasknum="${task.taskNum}" class="day-table-place-title">
                                        <div class="click-overlay"></div>
                                        <div class="click-overlay-edit"></div>
                                    </div>    
                                        </td>`;

                        }
                        // 이동 task 일때
                        else {
                            dayTable += `
                                    <td colspan="2">${task.task}</td>`;
                        }

                        // Add duration and cost columns
                        dayTable += `
                                <td class="day-table-td-duration"><input type="text" value="${durationHour}" disabled class="day-table-duration-hour"></td>
                                <td>시간</td>
                                <td class="day-table-td-duration"><input type="text" value="${durationMinute}" disabled class="day-table-duration-minute"></td>
                                <td>분</td>
                                <td class="day-table-td-cost"><input type="text" class="day-table-cost" data-daynum-cost="${task.dateN}" value="${task.cost}" disabled></td>
                                <td>원</td>`;

                        if (task.task !== '이동')
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


function updateTaskPlace() {
    $('.theme-button').css({
        'border': 'double 2px hotpink',
        'border-radius': '20px',
        'animation': 'vibration 0.5s infinite',
        'background-color' : '#f0f0f0'
    });
    clearThemeMarkers();
    clickCountThemeButton = 0;
    previousTheme = '';
    currentTheme = '';
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

    tr.find('input').css('background-color', 'lightgrey');

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

    $.ajax({
        url: 'custom/updateDuration',
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
        url: '/custom/getPlan',
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

                if (task.task !== '이동') {
                    const marker = new naver.maps.Marker({
                        map: map,
                        position: new naver.maps.LatLng(task.place.mapY, task.place.mapX),
                        title: task.place.titleKr,
                        taskNum: task.taskNum,
                        placeId: task.place.placeId,
                        icon: {
                            content: `<svg xmlns="http://www.w3.org/2000/svg" height="50px" viewBox="0 -960 960 960" width="50px" fill="#ff9999" stroke="#000000" stroke-width="5px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
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

    $.ajax({
        url: 'custom/placeInfoMore',
        type: 'post',
        data: {placeId: placeId},
        success: function (placeDTO) {

            let heritageIcon = placeDTO.heritage ? '<img src="../images/customPlan/heritage.png" class="info-table-badge" data-badge="heritage">' : '';
            let barrierFreeIcon = placeDTO.barrierFree ? '<img src="../images/customPlan/barrier.png" class="info-table-badge" data-badge="barrier">' : '';
            let petFriendlyIcon = placeDTO.petFriendly ? '<img src="../images/customPlan/pet.png" class="info-table-badge" data-badge="pet">' : '';
            let feeInfoKr = placeDTO.feeInfoKr == null ? '' : placeDTO.feeInfoKr;
            let saleItemKr = placeDTO.saleItemKr == null ? '' : placeDTO.saleItemKr;


            let infoMoreTable = `
                         <div class="info-more-table-box">
                            <table class="info-table">
                                <thead>     
                                    <tr class="info-table-basic">
                                        <th class="info-table-title" colspan="2">
                                            <h4 class="in-title">${placeDTO.titleKr}</h4>
                                            <span>${heritageIcon}</span>
                                            <span>${barrierFreeIcon}</span>
                                            <span>${petFriendlyIcon}</span>
                                        </th>
                                        <td class="add-task-button">                                   
                                            <img src="../images/customPlan/add-schedule.png" class="info-table-add-task-image">
                                        </td>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>    
                                        <th class="info-table-th"><img src="../images/customPlan/clock.png" class="info-table-images"></th>
                                        <td class="info-table-usetime">${placeDTO.usetimeKr}</td>
                                    </tr>
                                    <tr>    
                                        <th class="info-table-th"><img src="../images/customPlan/restday.png" class="info-table-images"></th>
                                        <td class="info-table-restday">${placeDTO.restdateKr}</td>
                                    </tr>
                                    <tr>    
                                        <th class="info-table-th"><img src="../images/customPlan/fee-Info-won.png" class="info-table-images"></th>
                                        <td class="info-table-fee-info">${feeInfoKr}</td>
                                    </tr>
                                    <tr>    
                                        <th class="info-table-th"><img src="../images/customPlan/thumbup.png" class="info-table-images"></th>
                                        <td class="info-table-sale-item">${saleItemKr}</td>
                                    </tr>
                                    <tr>
                                        <th class="info-table-th"><img src="../images/customPlan/description.png" class="info-table-images"></th>
                                        <td class="info-table-description" rowspan="2">${placeDTO.overviewKr}</td>
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

// taskList 클릭시(마커 색 변경, 일정표 해당 tr 색 변경)
function selectTaskList(input) {
    $(this).on('click', function () {
        let tr = input.closest('tr');
        let clickedTaskNum = input.data('tasknum'); // 클릭한 tr의 data-tasknum 가져오기

        // (1) task-list tr 배경색상 설정
        $('[class^="task-list"]').removeClass('selected');
        tr.addClass('selected');

        // (3) info-List 배경색상 설정
        $('[class^="place-info-section"]').removeClass('selected');
        $('.place-info-section' + clickedTaskNum).addClass('selected');

        // (4) 마커색상 설정
        planAllMarkers.forEach(function (marker) {
            if (marker.taskNum === clickedTaskNum) {
                // 클릭한 taskNum과 일치하는 마커에 대해 아이콘 변경
                marker.setIcon({
                    content: `<svg xmlns="http://www.w3.org/2000/svg" height="50px" viewBox="0 -960 960 960" width="50px" fill="#ff0000" stroke="#000000" stroke-width="5px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
                    anchor: new naver.maps.Point(11, 35)
                });
                marker.setZIndex(100); // 선택된 마커를 위에 표시
            } else {
                // 선택되지 않은 마커에 대해 아이콘 변경
                marker.setIcon({
                    content: `<svg xmlns="http://www.w3.org/2000/svg" height="50px" viewBox="0 -960 960 960" width="50px" fill="#ff9999" stroke="#000000" stroke-width="5px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
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
    });
}


// 일정마커 클릭시, planMarkerKey = taskNum
function selectPlanMarker(markerKey, planMarkerPlaceId) {
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
                content: `<svg xmlns="http://www.w3.org/2000/svg" height="50px" viewBox="0 -960 960 960" width="50px" fill="#ff0000" stroke="#000000" stroke-width="5px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
                size: new naver.maps.Size(22, 35),
                anchor: new naver.maps.Point(11, 35)
            });
        } else {
            // 나머지 일정마커 색상 초기화
            marker.setIcon({
                content: `<svg xmlns="http://www.w3.org/2000/svg" height="50px" viewBox="0 -960 960 960" width="50px" fill="#ff9999" stroke="#000000" stroke-width="5px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
                size: new naver.maps.Size(22, 35),
                anchor: new naver.maps.Point(11, 35)
            });
        }
    });

    // 테마마커 색상 초기화
    themeAllMarkers.forEach(marker => {
        marker.setIcon({
            content: `<svg xmlns="http://www.w3.org/2000/svg" height="35px" viewBox="0 -960 960 960" width="35px" fill="#7FFF00" stroke="#000000" stroke-width="5px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
            size: new naver.maps.Size(22, 35),
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
    console.log('ajax 전 previousTheme:'+ previousTheme);
    
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
            url: '/custom/themeMarkers',
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
                        let marker = new naver.maps.Marker({
                            map: map,
                            position: new naver.maps.LatLng(placeByTheme.mapY, placeByTheme.mapX),
                            title: placeByTheme.titleKr,
                            placeId: placeByTheme.placeId,
                            icon: {
                                content: `<svg xmlns="http://www.w3.org/2000/svg" width="20px" height="20px"
                                                    viewBox="0 0 20 20" fill="#006400" stroke="#00000 stroke-width="0.3px" stroke-opacity="0.24">
                                                    <circle cx="8" cy="8" r="6"/></svg>`,
                                anchor: new naver.maps.Point(13, 13)
                            },
                            zIndex: 100
                        });

                        let contents = [
                            '<div class="marker-place-info">',
                            `<img src="${placeByTheme.originImgUrl}">`,
                            `   <h4>${placeByTheme.titleKr}</h4>`,
                            `   <p>${placeByTheme.addressKr}<br />`,
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
                console.log('ajax 후 previousTheme:'+ previousTheme);
                
            },
            error: function (error) {
                console.error('에러발생:', error);
                alert('테마정보 로드 실패');
            }
        }); // ajax문
    } // previousTheme !== currentTheme if문
}

// 줌레벨에 따른 테마마커 모양 변화
naver.maps.addEventListener(map, 'zoom_changed', function () {
    let currentZoom = map.getZoom();
    console.log('줌레벨');
    // Conditional logic based on zoom level
    $.each(themeAllMarkers, function (index, themeMarker) {
        if (currentZoom > 15) {
            // Use a larger circle for higher zoom levels
            themeMarker.setIcon({
                content: `<svg xmlns="http://www.w3.org/2000/svg" height="40px" viewBox="0 -960 960 960" width="35px" fill="#006400" stroke="#000000" stroke-width="5px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
                anchor: new naver.maps.Point(15, 15) // Adjust anchor accordingly
            });
        } else if (currentZoom > 8) {
            // Medium-sized circle for mid zoom levels
            themeMarker.setIcon({
                content: `<svg xmlns="http://www.w3.org/2000/svg" width="20px" height="20px"
                                viewBox="0 0 20 20" fill="#006400" stroke="#000000" stroke-width="0.3px" stroke-opacity="0.24">
                                <circle cx="10" cy="10" r="8"/></svg>`,
                anchor: new naver.maps.Point(10, 10) // Adjust anchor accordingly
            });
        } else {
            // Small circle for lower zoom levels
            themeMarker.setIcon({
                content: `<svg xmlns="http://www.w3.org/2000/svg" width="10px" height="10px"
                                viewBox="0 0 10 10" fill="#006400" stroke="#000000" stroke-width="0.3px" stroke-opacity="0.24">
                                <circle cx="5" cy="5" r="4"/></svg>`,
                anchor: new naver.maps.Point(5, 5) // Adjust anchor accordingly
            });
        }
    });
});


// 테마마커 선택시
function selectThemeMarker(markerKey) {

    console.log(markerKey); //
    // 마커 목록 순회
    themeAllMarkers.forEach(marker => {
        if (marker.placeId === markerKey) {
            // 선택된 마커 강조
            marker.setIcon({
                content: `<svg xmlns="http://www.w3.org/2000/svg" height="35px" viewBox="0 -960 960 960" width="35px" fill="#228B22" stroke="#000000" stroke-width="5px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
                size: new naver.maps.Size(22, 35),
                anchor: new naver.maps.Point(11, 35)
            });
        } else {
            // 나머지 마커는 기본 색상으로 초기화
            marker.setIcon({
                content: `<svg xmlns="http://www.w3.org/2000/svg" height="35px" viewBox="0 -960 960 960" width="35px" fill="#7FFF00" stroke="#000000" stroke-width="5px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
                size: new naver.maps.Size(22, 35),
                anchor: new naver.maps.Point(11, 35)
            });
        }
    });
    planAllMarkers.forEach(marker => {
        marker.setIcon({
            content: `<svg xmlns="http://www.w3.org/2000/svg" height="50px" viewBox="0 -960 960 960" width="50px" fill="#ff9999" stroke="#000000" stroke-width="5px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
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

    // console.log(visiblePlanMarkerKeyList);

    // Clear existing list
    while (infoList.firstChild) {
        infoList.removeChild(infoList.firstChild);
    }

    // Loop through visible marker keys
    for (const key of visiblePlanMarkerKeyList) {
        const task = taskListData.find(t => t.taskNum === key); // Assuming taskList is globally accessible
        // Create the HTML structure for each visible marker
        let img = task.place.originImgUrl === '' ?
            '<img src="../images/customPlan/nonImg.png" class="place-info-imgNone">' :
            `<img src="${task.place.originImgUrl}" class="place-info-img">`;

        infoList.innerHTML += `
            <div class="place-info-section${task.place.placeId}" >
                ${img}
                <h3 class="place-info-title">${task.place.titleKr}</h3>
                <img src="../images/customPlan/more.png" class="place-info-more" data-place-id="${task.place.placeId}">
                <span class="place-info-address">${task.place.addressKr}</span>
                <span class="place-info-contentsType">${task.place.contentsType}</span>
                <img src="../images/customPlan/infocenter.png" class="place-info-infocenterImg">
                <span class="place-info-infocenter">${task.place.infocenter}</span>
            </div>
        `;
    }
}

// 테마마커 장소정보List 출력함수
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
        let img = place.originImgUrl === '' ?
            '<img src="../images/customPlan/nonImg.png" class="place-info-imgNone">' :
            `<img src="${place.originImgUrl}" class="place-info-img">`;

        infoList.innerHTML += `
            <div class="place-info-section${place.placeId}" >
                ${img}
                <h3 class="place-info-title">${place.titleKr}</h3>
                <img src="../images/customPlan/more.png" class="place-info-more" data-place-id="${place.placeId}">
                <span class="place-info-address">${place.addressKr}</span>
                <span class="place-info-contentsType">${place.contentsType}</span>
                <img src="../images/customPlan/infocenter.png" class="place-info-infocenterImg">
                <span class="place-info-infocenter">${place.infocenter}</span>
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






