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
        $('.day1-tasks').css('display', 'none');
        $('.day2-tasks').css('display', 'none');
        $('.day3-tasks').css('display', 'none');
        $('.day4-tasks').css('display', 'none');
        $('.day5-tasks').css('display', 'none');
        $('.day6-tasks').css('display', 'block');
    });
})


//마커정보를 담을 전역변수 마크배열


$(document).ready(function () {
    // 지도 초기화 (경복궁이 시작점, 줌 인터페이스 설정) long경도 큰 숫자 y / lati위도 작은 숫자 x
    let firstLat = $('#firstLat').val();
    let firstLng = $('#firstLng').val();



    // 로딩되자 마자 보이는 마커 : 플랜 일정에 포함되어 있는 장소만
    allPlanMarker(map);

    // 일자별 버튼 생성 및 데이 플랜 호출
    ['all', '1day', '2day', '3day', '4day', '5day', '6day'].forEach(function (day) {
        $('#plan-' + day + '-button').click(allPlanMarker);
    });

    $('#theme-shopping-button').click(themeMarker);
    $('#theme-food-button').click(themeMarker);
    $('#theme-cafe-button').click(themeMarker);
    $('#theme-history-button').click(themeMarker);
    $('#theme-recreation-button').click(themeMarker);
    $('#theme-culture-button').click(themeMarker);
    $('#theme-healing-button').click(themeMarker);
    $('#theme-landmark-button').click(themeMarker);
    $('#theme-experience-button').click(themeMarker);
    $('#theme-nature-button').click(themeMarker);
    $('#theme-walking-button').click(themeMarker);
    $('#theme-leisure-button').click(themeMarker);
});

let allMarkers = [];
// 플랜별 & 일자별 마커 출력 함수
function allPlanMarker(map) {
    let planNum = $('#planNum').val();
    let dayNum = $(this).data('daynum');
    if (dayNum == null) {
        dayNum = 0;
    }
    clearMarkers();

    $.ajax({
        url: '/custom/allPlanMarker',
        type: 'post',
        data: {
            planNum: planNum,
            dayNum: dayNum
        },
        success: function (planLocations) { // 해당 플랜에 해당하는 장소의 마커정보를 가져옴
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
                let marker = new naver.maps.Marker({
                    position: new naver.maps.LatLng(location.place.mapY, location.place.mapX),
                    map: map
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
// function themeMarker() {
//     let theme = $(this).data('theme');
//     console.log(theme);
//     $.ajax({
//         url: '/custom/themeMarker',
//         type: 'post',
//         data: {theme : theme},
//         success : function(themeLocations) {
//             alert('성공');
//             let map = new naver.maps.Map('mapBox', {
//                 zoom: 15,
//                 scaleControl: false,
//                 logoControl: false,
//                 mapDataControl: false,
//                 zoomControl: true,
//             });
//             let markers = [];
//             $.each(themeLocations, function(index, location) {
//                 let marker = new naver.maps.Marker({
//                     position: new naver.maps.LatLng(location.mapX, location.mapY),
//                     map: map
//                 });
//             }); // 반복문 끝
//         },
//         error: function(error) {
//             console.error('에러발생:', error);
//             alert('데이터 전송 실패');
//         }
//     });
// }


