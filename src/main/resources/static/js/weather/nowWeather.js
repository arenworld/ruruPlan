$(document).ready(function(){
    nowWeather();
});

/*현재 하늘상태(sky), 강수형태(pty), 습도(reh), 기온(tmp)*/
function nowWeather() {  // Text API 호출 함수

    const now = new Date();  // 현재 날짜 객체 생성

    // YYMMDD 조합
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate() - 1).padStart(2, '0');

    const today = `${year}${month}${day}`;

    //시간 HH 조합
    const hours = String(now.getHours()).padStart(2, '0');

    let apiUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=OLmqqqBz%2FpgJKAez5lZ6oIU6Dgn2aRuV00bmzoOZ%2FFPcHe4ZMCLTyeyFAT1CxRXXTgfxm%2F4IWoXeqj7YIuFDfw%3D%3D&pageNo=1&numOfRows=1000&dataType=JSON&base_date="
        + today + "&base_time=2300&nx=60&ny=127";       //base_time은 2300으로 고정

    /*오늘 일기 예보에 따른 지금 시간 날씨 값 요청*/
    $.getJSON(
        apiUrl,
        function (data) {
            //현재 시간대 response. tmp, sky REH, TMN
            // TMX
            let tmp   = parseInt(hours)* 12;
            let sky   = parseInt(hours)* 12 + 5;
            let pty   = parseInt(hours)* 12 + 6;
            let reh   = parseInt(hours)* 12 + 10;
            if (tmp > 83) {     // 최저기온 분기점
                tmp += 1;
                sky += 1;
                pty += 1;
                reh += 1;
            }
            if (tmp > 192) {    //최고기온 분기점
                tmp += 1;
                sky += 1;
                pty += 1;
                reh += 1;
            }

            // 화면에 출력
            document.getElementById('tem').innerHTML= data.response.body.items.item[tmp].fcstValue + "°C";
            document.getElementById('hum').innerHTML= data.response.body.items.item[reh].fcstValue + "%";

            // 날씨에 따른 이모티콘 변경
            var icon = document.getElementById("weather"); // img 태그 선택
            var iconSrc = "/images/home/";

            /*날씨 구성 [맑음. 구름많음, 흐림, 눈, 비]*/
            // weatherCondition에 따라 아이콘 경로를 설정
            switch (parseInt(data.response.body.items.item[pty].fcstValue)) {
                case 0:
                    switch (parseInt(data.response.body.items.item[sky].fcstValue)) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                            iconSrc += "맑음.png";
                            break;
                        case 6:
                        case 7:
                        case 8:
                            iconSrc += "구름.png";
                            break;
                        case 9:
                        case 10:
                            iconSrc += "흐림.png";
                            break;
                        default:
                            iconSrc += "날씨기본.png"; // 기본 이미지 설정
                    }
                    break;
                case 1: //비
                case 4: //소나기
                    iconSrc += "비.png";
                    break;
                case 2:
                    iconSrc += "눈,비.png";
                case 3: //눈
                    iconSrc += "눈.png";
                    break;
                default:
                    iconSrc += "날씨기본.png"; // 기본 이미지 설정
            }
            // img 태그의 src 속성 변경
            icon.src = iconSrc;

            // 현재 시간대 날씨 데이터 콘솔에 불러오기
            console.log(data.response.body.items.item[tmp].fcstTime);   //현재 시간대 (1시간 단위)
            console.log(data.response.body.items.item[tmp].fcstValue);  //현재 온도
            console.log(data.response.body.items.item[sky].fcstValue);  //현재 하늘: [맑음 0 ～ 5][구름많음	6 ～ 8][흐림	9 ～ 10]
            console.log(data.response.body.items.item[pty].fcstValue);  //현재 강수형태: 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
            console.log(data.response.body.items.item[reh].fcstValue);  //현재 습도
        }
    );
}