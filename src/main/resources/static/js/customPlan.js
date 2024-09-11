$(document).ready(function () {
    $('#plan-all').css('display', 'block');
    $('#plan-all-button').click(function () {
        $('.plan-details').css('display', 'block');
    });

    $('#plan-1day-button').click(function () {
        $('.day1-tasks').css('display', 'block');
        $('.day2-tasks').css('display', 'none');
        $('.day3-tasks').css('display', 'none');
        $('.day4-tasks').css('display', 'none');
        $('.day5-tasks').css('display', 'none');
        $('.day6-tasks').css('display', 'none');
    });

    $('#plan-2day-button').click(function () {
        $('.day1-tasks').css('display', 'none');
        $('.day2-tasks').css('display', 'block');
        $('.day3-tasks').css('display', 'none');
        $('.day4-tasks').css('display', 'none');
        $('.day5-tasks').css('display', 'none');
        $('.day6-tasks').css('display', 'none');
    });

    $('#plan-3day-button').click(function () {
        $('.day1-tasks').css('display', 'none');
        $('.day2-tasks').css('display', 'none');
        $('.day3-tasks').css('display', 'block');
        $('.day4-tasks').css('display', 'none');
        $('.day5-tasks').css('display', 'none');
        $('.day6-tasks').css('display', 'none');
    });

    $('#plan-4day-button').click(function () {
        $('.day1-tasks').css('display', 'none');
        $('.day2-tasks').css('display', 'none');
        $('.day3-tasks').css('display', 'none');
        $('.day4-tasks').css('display', 'block');
        $('.day5-tasks').css('display', 'none');
        $('.day6-tasks').css('display', 'none');
    });

    $('#plan-5day-button').click(function () {
        $('.day1-tasks').css('display', 'none');
        $('.day2-tasks').css('display', 'none');
        $('.day3-tasks').css('display', 'none');
        $('.day4-tasks').css('display', 'none');
        $('.day5-tasks').css('display', 'block');
        $('.day6-tasks').css('display', 'none');
    });

    $('#plan-6day-button').click(function () {

    });
})


//마커정보를 담을 전역변수 마크배열


$(document).ready(function () {
    // 로딩되자 마자 보이는 마커 : 플랜 일정에 포함되어 있는 장소만
    planMarkers(map);

    // 일자별 버튼 생성 및 데이 플랜 호출
    const days = ['all', '1day', '2day', '3day', '4day', '5day', '6day'];
    days.forEach(function (day) {
        $('#plan-' + day + '-button').click(planMarkers);
    });

    $('.day1-tasks').css('display', 'none');
    $('.day2-tasks').css('display', 'none');
    $('.day3-tasks').css('display', 'none');
    $('.day4-tasks').css('display', 'none');
    $('.day5-tasks').css('display', 'none');
    $('.day6-tasks').css('display', 'block');
    // 테마별 버튼 생성 및 테마 정보 호출, 레포츠 처리 어떻게 해야 되지?
    const themes = ['쇼핑', '음식', '카페', '역사', '문화', '힐링', '랜드마크', '체험', '레포츠']
    themes.forEach(function (theme) {
        $('#theme-' + theme + '-button').click(themeMarkers);
    });
});

//마커정보 담는 전역변수 마커배열 ***
let allMarkers = [];
let planLocations =null;
// 플랜별 & 일자별 마커 출력 함수
function planMarkers() {
    let planNum = $('#planNum').val();
    let dayNum = $(this).data('daynum');
    if (dayNum == null) {
        dayNum = 0;
    }
    clearMarkers();

    $.ajax({
        url: '/custom/planMarkers',
        type: 'post',
        data: {
            planNum: planNum,
            dayNum: dayNum
        },
        success: function (planLocations) { // 해당 플랜에 해당하는 장소의 마커정보를 가져옴
            planLocations = planLocations;

            let mapOptions = {
                center: new naver.maps.LatLng(planLocations[0].place.mapY, planLocations[0].place.mapX),
                zoom: 16,
                miniZoom: 15,
                maxZoom: 20,
                zoomControl: true,
                zoomControlOptions: {
                    style: naver.maps.ZoomControlStyle.SMALL,
                    position: naver.maps.Position.TOP_RIGHT
                }
            };

            let map = new naver.maps.Map(document.getElementById('map'), mapOptions);

            //List<TaskDTO> planLocations
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
                allMarkers.push(marker); // 생성된 마커 배열에 저장
            }); // 반복문 끝

            // 일정 클릭 시 해당 마커 위치로 이동
            $('.placeTitle').each(function () {
                $(this).on('click', function () {
                    let clickedTaskNum = $(this).data('tasknum'); // 클릭한 td의 data-tasknum 가져오기

                    // 해당 taskNum과 일치하는 마커 찾기
                    let targetMarker = allMarkers.find(function (marker) {
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

function clearMarkers() {
    $.each(allMarkers, function (index, marker) {
            marker.setMap(null); // 마커를 지도에서 제거
    });
    allMarkers.length = 0; // 배열 비우기
}

// 테마별 마커 출력 함수
let themeAllMarkers = [];
function themeMarkers(planLocations) {
       let theme = $(this).data('theme');
       alert('동작함');
    $.ajax({
        url: '/custom/themeMarkers',
        type: 'post',
        data: {theme : theme},
        success : function(themeLocations) {
            alert('성공');
            console.log(themeLocations);

            let mapOptions = {
                center: new naver.maps.LatLng(themeLocations[0].mapY, themeLocations[0].mapX),
                zoom: 16,
                miniZoom: 15,
                maxZoom: 20,
                zoomControl: true,
                zoomControlOptions: {
                    style: naver.maps.ZoomControlStyle.SMALL,
                    position: naver.maps.Position.TOP_RIGHT
                }
            }
            let map = new naver.maps.Map(document.getElementById('map'), mapOptions);

            $.each(themeLocations, function(index, location) {
                let marker = new naver.maps.Marker({
                    position: new naver.maps.LatLng(location.mapY, location.mapX),
                    map: map
                });
                themeAllMarkers.push(marker);
            }); // 반복문 끝
        },
        error: function(error) {
            console.error('에러발생:', error);
            alert('데이터 전송 실패');
        }
    });
 }


