
//버튼번호 전역변수, 들어오자마자 0이고 그뒤로는 클릭한 버튼의 일자번호를 가져옴.
let dayNumOfButton = 0;
//마커정보 담는 전역변수 마커배열 ***
var planAllMarkers = [];
let themeAllMarkers = [];
var map = null;
var mapOptions = {};

$(document).ready(function () {

    // 로딩되자 마자 보이는 마커 : 플랜 일정에 포함되어 있는 장소만
    planMarkers(dayNumOfButton);

    // 테마별 버튼 생성 및 테마 정보 호출, 레포츠 처리 어떻게 해야 되지?
    const themes = ['쇼핑', '음식', '카페', '역사', '문화', '힐링', '랜드마크', '체험', '레포츠']
    themes.forEach(function (theme) {
        $('#theme-' + theme + '-button').click(themeMarkers);
    });

    // 일자별 버튼 생성 및 데이 플랜 호출
    const days = ['allday', '1day', '2day', '3day', '4day', '5day', '6day'];
    days.forEach(function (day) {
        //const dayNum = day.replace('day', ''); // 숫자부분만 남긴다
        $('#plan-' + day + '-button').click(function() {
            dayNumOfButton = $(this).data('daynum-button');

            planMarkers(dayNumOfButton);

            // 테이블 번호랑 비교하기
            $('.planTables').each(function() {
                let dayNumOfTable = $(this).data('daynum-table');
                console.log(dayNumOfTable);
                console.log(dayNumOfButton);
                if(dayNumOfButton === 0) {
                    $('.planTables').css('display', 'block');
                } else if (dayNumOfButton === dayNumOfTable) {
                    $(this).css('display', 'block');
                } else {
                    $(this).css('display','none');
                }
            });
        });
    });
});



// 플랜별 & 일자별 마커 출력 함수
function planMarkers(dayNumOfButton) {
    let planNum = $('#planNum').val();

    if (dayNumOfButton == null) {
        dayNumOfButton = 0;
    }
    clearPlanMarkers();

    $.ajax({
        url: '/custom/planMarkers',
        type: 'post',
        data: {
            planNum: planNum,
            dayNumOfButton: dayNumOfButton
        },
        success: function (planLocations) { // 해당 플랜에 해당하는 장소의 마커정보를 가져옴
            mapOptions = {
                center: new naver.maps.LatLng(planLocations[0].place.mapY, planLocations[0].place.mapX),
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

            //List<TaskDTO> planLocations
            $.each(planLocations, function (index, location) {
                const marker = new naver.maps.Marker({
                    map: map,
                    position: new naver.maps.LatLng(location.place.mapY, location.place.mapX),
                    title: location.place.placeId,
                    icon: {
                        content: `<svg xmlns="http://www.w3.org/2000/svg" height="40px" viewBox="0 -960 960 960" width="35px" fill="#c4c0ff" stroke="#000000" stroke-width="5px"><path d="M480-480q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Zm0 400Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Z"/></svg>`,
                        // content: `<img src="/images/customPlan/marker-red.png"
                        //   style="width:25px; height:33px;"
                        //   alt="marker">`,
                        anchor: new naver.maps.Point(11, 35)
                    },
                    zIndex: 100
                });
                marker.taskNum = location.taskNum; // 마커에 taskNum 속성 추가
                planAllMarkers.push(marker); // 생성된 마커 배열에 저장
            }); // 반복문 끝

            // 일정 클릭 시 해당 마커 위치로 이동
            $('.placeTitle').each(function () {
                $(this).on('click', function () {
                    let clickedTaskNum = $(this).data('tasknum'); // 클릭한 td의 data-tasknum 가져오기

                    // 해당 taskNum과 일치하는 마커 찾기
                    let targetMarker = planAllMarkers.find(function (marker) {
                        return marker.taskNum === clickedTaskNum;
                    });

                    if (targetMarker) {
                        let position = targetMarker.getPosition(); // 마커의 위치 가져오기
                        map.setCenter(position); // 지도의 중심을 해당 마커 위치로 이동
                        map.setZoom(16); // 줌 레벨 조정 (필요에 따라)
                    }
                });
            });
        },
        error: function (error) {
            console.error('에러발생:', error);
            alert('전체 일정 데이터 전송 실패');
        }
    });
}

function clearPlanMarkers() {
    $.each(planAllMarkers, function (index, marker) {
            marker.setMap(null); // 마커를 지도에서 제거
    });
    planAllMarkers.length = 0; // 배열 비우기
}

function clearThemeMarkers() {
    $.each(themeAllMarkers, function (index, marker) {
        marker.setMap(null); // 마커를 지도에서 제거
    });
    themeAllMarkers.length = 0; // 배열 비우기
}

// 테마별 마커 출력 함수

function themeMarkers() {
    let theme = $(this).data('theme');
    let planNum = $('#planNum').val();
    clearThemeMarkers();
    $.ajax({
        url: '/custom/themeMarkers',
        type: 'post',
        data: {theme : theme
            , planNum : planNum
            , dayNumOfButton : dayNumOfButton},
        success : function(planAndThemeLocations) {
            let themeLocations = planAndThemeLocations.themeLocations;
            let planLocations = planAndThemeLocations.planLocations;

            let bounds = map.getBounds();
            $.each(themeLocations, function(index, location) {
                let marker = new naver.maps.Marker({
                    position: new naver.maps.LatLng(location.mapY, location.mapX),
                    map: map
                });
                themeAllMarkers.push(marker);
            }); // 테마정보 반복문 끝

            naver.maps.addEventListener(map, 'idle', function() {
                updateMarkers(map, themeAllMarkers, planAllMarkers);
            });

            $.each(planLocations, function (index, location) {
                const marker = new naver.maps.Marker({
                    map: map,
                    position: new naver.maps.LatLng(location.place.mapY, location.place.mapX),
                    icon: {
                        content: `<img src="/images/customPlan/marker-red.png"
                           style="width:25px; height:33px;"
                           alt="marker">`,
                        anchor: new naver.maps.Point(11, 35)
                    },
                    zIndex: 100
                });
                marker.taskNum = location.taskNum; // 마커에 taskNum 속성 추가
                planAllMarkers.push(marker); // 생성된 마커 배열에 저장
            }); // 일정정보 반복문 끝

            // 일정 클릭 시 해당 마커 위치로 이동
            $('.placeTitle').each(function () {
                $(this).on('click', function () {
                    let clickedTaskNum = $(this).data('tasknum'); // 클릭한 td의 data-tasknum 가져오기

                    // 해당 taskNum과 일치하는 마커 찾기
                    let targetMarker = planAllMarkers.find(function (marker) {
                        return marker.taskNum === clickedTaskNum;
                    });

                    if (targetMarker) {
                        let position = targetMarker.getPosition(); // 마커의 위치 가져오기
                        map.setCenter(position); // 지도의 중심을 해당 마커 위치로 이동
                        map.setZoom(16); // 줌 레벨 조정 (필요에 따라)
                    }
                });
            });// 일정정보에 클릭이벤트 추가 해당마커로 이동하기
        },
        error: function(error) {
            console.error('에러발생:', error);
            alert('데이터 전송 실패');
        }
    });
 }

function updateMarkers(map, themeAllMarkers, planAllMarkers) {
    var mapBounds = map.getBounds();
    var marker, position;

    for (var i = 0; i < themeAllMarkers.length; i++) {
        marker = themeAllMarkers[i];

        if(mapBounds.hasLatLng(position)) {
            showMarker(map, marker);
        } else {
            hideMarker(map,marker);
        }
    } // themeAllMarkers 리스트 체크

    for (var i = 0; i < planAllMarkers.length; i++) {
        marker = planAllMarkers[i];

        if(mapBounds.hasLatLng(position)) {
            showMarker(map, marker);
        } else {
            hideMarker(map,marker);
        }
    } // planAllMarkers 리스트 체크
}

function showMarker(map, marker) {
    if (marker.getMap()) return;
    marker.setMap(map);
}

function hideMarker(map, marker) {
    if(!marker.getMap()) return;
    marker.setMap(null);
}