$(document).ready(function(){
    nowWeather();
    daysWeather();
});

/*현재 하늘상태(sky), 강수형태(pty), 습도(reh), 기온(tmp)*/
function nowWeather() {  // Text API 호출 함수
    alert("실행됨");
    const now = new Date();  // 현재 날짜 객체 생성

    // YYMMDD 조합
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate() - 1).padStart(2, '0');

    const today = `${year}${month}${day}`;

    //시간 HH 조합
    const hours = String(now.getHours()).padStart(2, '0');

    let apiUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=OLmqqqBz%2FpgJKAez5lZ6oIU6Dgn2aRuV00bmzoOZ%2FFPcHe4ZMCLTyeyFAT1CxRXXTgfxm%2F4IWoXeqj7YIuFDfw%3D%3D&pageNo=1&numOfRows=1000&dataType=JSON&base_date="
        + today + "&base_time=2300&nx=60&ny=127";       //base_time은 0500으로 고정

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

            // 현재 시간대 날씨 데이터 불러오기
            console.log(data.response.body.items.item[tmp].fcstTime);   //현재 시간대 (1시간 단위)
            console.log(data.response.body.items.item[tmp].fcstValue);  //현재 온도
            console.log(data.response.body.items.item[sky].fcstValue);  //현재 하늘: [맑음 0 ～ 5][구름많음	6 ～ 8][흐림	9 ～ 10]
            console.log(data.response.body.items.item[pty].fcstValue);  //현재 강수형태: 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
            console.log(data.response.body.items.item[reh].fcstValue);  //현재 습도
        }
    );
}

/**
 * 준비물 페이지
 * 오늘(#sky, @pty, *tmn, *tmx, #reh)
 * 내일(#sky, @pty, *tmn, *tmx, #reh)
 * 모레(#sky, @pty, *tmn, *tmx, #reh)
 * #평균값 / @존재 유무(비,눈) / *고정값
 */
function daysWeather() {
    alert("예보 정보 실행");
    const now = new Date();  // 현재 날짜 객체 생성

    // YYMMDD 조합
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate() - 1).padStart(2, '0');     // 어제 23시의 예보는 오늘 00시~모레 23시까지의 예보

    const today = `${year}${month}${day}`;

    //시간 HH 조합
    const hours = String(now.getHours()).padStart(2, '0');

    let apiUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=OLmqqqBz%2FpgJKAez5lZ6oIU6Dgn2aRuV00bmzoOZ%2FFPcHe4ZMCLTyeyFAT1CxRXXTgfxm%2F4IWoXeqj7YIuFDfw%3D%3D&pageNo=1&numOfRows=1000&dataType=JSON&base_date="
        + today + "&base_time=2300&nx=60&ny=127";       //base_time은 0500으로 고정

    /*일기 예보에 오늘 날씨 값 요청*/
    $.getJSON(
        apiUrl,
        function (data) {
            //현재 시간대 response. tmp, sky REH, TMN
            var tmn = [84, 374,664];      //각 날짜 최저기온 배열 번호
            var tmx = [193, 483,773];       //각 날짜 최고기온 배열 번호
            let sky   = parseInt(hours)* 12 + 5;
            let pty   = parseInt(hours)* 12 + 6;
            let reh   = parseInt(hours)* 12 + 10;

            if (sky > tmn[0]) {     // 최저기온 분기점
                sky += 1;
                pty += 1;
                reh += 1;
            }
            if (sky > tmx[0]) {    //최고기온 분기점
                sky += 1;
                pty += 1;
                reh += 1;
            }
         /*   console.log(data);*/

            // 오늘 날씨 데이터 불러오기
            console.log(data.response.body.items.item[tmn[0]].fcstValue);   //오늘 최저 온도
            console.log(data.response.body.items.item[tmn[0]].fcstTime);   //오늘 최저 기온 시간
            console.log(data.response.body.items.item[tmx[0]].fcstValue);  //오늘 최고 온도
            console.log(data.response.body.items.item[tmx[0]].fcstTime);  //오늘 최고 기온 시간
            console.log(data.response.body.items.item[sky].fcstValue);  //현재 하늘: [맑음 0 ～ 5][구름많음	6 ～ 8][흐림	9 ～ 10]
            console.log("현재 강수 형태 : " + data.response.body.items.item[pty].fcstValue);  //현재 강수형태: 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
            console.log(data.response.body.items.item[reh].fcstValue);  //현재 습도

            // 내일 날씨 데이터 불러오기
            console.log(data.response.body.items.item[tmn[1]].fcstValue);   //내일 최저기온
            console.log(data.response.body.items.item[tmx[1]].fcstValue);   //내일 최고기온

            //내일 평균 하늘 데이터
            var avgSky = 0;
            for(let i = 295; i<580; i+=12) {
                if (i === (tmn[1] + 5)) { i++; }
                if (i === (tmx[1] + 5)) { i++; }
                avgSky += parseInt(data.response.body.items.item[i].fcstValue);
            }
            console.log ("sum값 : " + avgSky)
            if (avgSky != 0) {
                avgSky = (avgSky/24).toFixed(2);
            }
            console.log(avgSky);
            if(avgSky >= 0 && avgSky <= 5) {
                console.log("내일의 평균하늘 상태" + avgSky + "맑음");
            }
            if(avgSky > 5 && avgSky <= 8) {
                console.log("내일의 평균하늘 상태" + avgSky + "구름많음");
            }
            if(avgSky > 8 && avgSky <= 10) {
                console.log("내일의 평균하늘 상태" + avgSky + "흐림");
            }

            //내일 강수형태
            var rainsnow = 0;
            for(let i = 296; i<580; i+=12) {
                if (i === (tmn[1] + 6)) { i++; }
                if (i === (tmx[1] + 6)) { i++; }
                if (data.response.body.items.item[i].fcstValue != 0) {
                    rainsnow = data.response.body.items.item[i].fcstValue;
                }
            }
            if (rainsnow == 0) {
                console.log("비예보 없음");
            }
            if(rainsnow == 1 && avgSky == 4) {
                console.log("비");
            }
            if(avgSky == 2) {
                console.log("눈/비");
            }
            if(avgSky == 3) {
                console.log("눈");
            }

            //내일 평균습도
            var avgReh = 0;
            for(let i = 300; i<580; i+=12) {
                if (i === (tmn[1] + 10)) { i++; }
                if (i === (tmx[1] + 10)) { i++; }
                avgReh += parseInt(data.response.body.items.item[i].fcstValue);
            }
            avgReh = (avgReh/24).toFixed(1);
            console.log("평균 습도 : " + avgReh + "%");


             // 모레 날씨 데이터 불러오기
            console.log(data.response.body.items.item[tmn[2]].fcstValue);   //모레 최저기온
            console.log(data.response.body.items.item[tmx[2]].fcstValue);   //모레 최고기온

            //모레 평균 하늘 데이터
            var avgSky = 0;
            for(let i = 585; i<881; i+=12) {
                if (i === (tmn[2] + 5)) { i++; }
                if (i === (tmx[2] + 5)) { i++; }
                avgSky += parseInt(data.response.body.items.item[i].fcstValue);
            }
            console.log ("sum값 : " + avgSky)
            if (avgSky != 0) {
                avgSky = (avgSky/24).toFixed(2);
            }
            console.log(avgSky);
            if(avgSky >= 0 && avgSky <= 5) {
                console.log("모레의 평균하늘 상태" + avgSky + "맑음");
            }
            if(avgSky > 5 && avgSky <= 8) {
                console.log("모레의 평균하늘 상태" + avgSky + "구름많음");
            }
            if(avgSky > 8 && avgSky <= 10) {
                console.log("모레의 평균하늘 상태" + avgSky + "흐림");
            }

            //모레 강수형태
            var rainsnow = 0;
            for(let i = 586; i<881; i+=12) {
                if (i === (tmn[2] + 6)) { i++; }
                if (i === (tmx[2] + 6)) { i++; }
                console.log(data.response.body.items.item[i].fcstTime +" : " +data.response.body.items.item[i].category + ":" + data.response.body.items.item[i].fcstValue);
                if (data.response.body.items.item[i].fcstValue != 0) {
                    rainsnow = data.response.body.items.item[i].fcstValue;
                }
            }
            if (rainsnow == 0) {
                console.log("비예보 없음");
            }
            if(rainsnow == 1 && avgSky == 4) {
                console.log("비");
            }
            if(avgSky == 2) {
                console.log("눈/비");
            }
            if(avgSky == 3) {
                console.log("눈");
            }

            //모레 평균습도
            var avgReh = 0;
            for(let i = 590; i<881; i+=12) {
                if (i === (tmn[2] + 10)) { i++; }
                if (i === (tmx[2] + 10)) { i++; }
                avgReh += parseInt(data.response.body.items.item[i].fcstValue);
            }
            avgReh = (avgReh/24).toFixed(1);
            console.log("평균 습도 : " + avgReh + "%");
        }
    );
}