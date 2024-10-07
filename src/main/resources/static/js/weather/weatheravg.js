$(document).ready(function(){
// 모달 요소
    var weatherModal = document.getElementById("wModal");

    $('#wea_img').on('click', function (event) {
        event.preventDefault();
        weatherModal.style.display = "block";
    });

    // 모달 닫기
    $('.close').on('click', function() {
        weatherModal.style.display = "none";
    })

    // 모달 외부 클릭 시 닫기
    window.onclick = function(event) {
        if (event.target == weatherModal) {
            weatherModal.style.display = "none";
        }
    }


    let sd = $('#sd').val();
    const startDate = sd.replace(/-/g, "") - 10000;

    let ed = $('#ed').val();
    const endDate = ed.replace(/-/g, "") - 10000;

    let lastY = $('#lastY').val();
    let tem = $('#temS').val();
    let rhm = $('#rhm').val();
    let rn = $('#avgrn').val();
    let em = $('#essentials').val();
    let e1 = $('#e1').val();
    let e2 = $('#e2').val();
    let e3 = $('#e3').val();
    let e4 = $('#e4').val();
    let e5 = $('#e5').val();
    let wm1 = $('#wm1').val();
    let wm2 = $('#wm2').val();
    let wm3 = $('#wm3').val();
    let wm4 = $('#wm4').val();

    const key = "OLmqqqBz%2FpgJKAez5lZ6oIU6Dgn2aRuV00bmzoOZ%2FFPcHe4ZMCLTyeyFAT1CxRXXTgfxm%2F4IWoXeqj7YIuFDfw%3D%3D"

    var xhr = new XMLHttpRequest();
    var url = 'http://apis.data.go.kr/1360000/AsosDalyInfoService/getWthrDataList'; /*URL*/
    var queryParams = '?' + encodeURIComponent('serviceKey') + '='+key; /*Service Key*/
    queryParams += '&' + encodeURIComponent('dataType') + '=' + encodeURIComponent('XML'); /*요청자료 형식*/
    queryParams += '&' + encodeURIComponent('dataCd') + '=' + encodeURIComponent('ASOS'); /*자료 분류 코드*/
    queryParams += '&' + encodeURIComponent('dateCd') + '=' + encodeURIComponent('DAY'); /*날짜 분류 코드*/
    queryParams += '&' + encodeURIComponent('startDt') + '=' + encodeURIComponent(startDate); /*조회 기간 시작일('startDate')*/
    queryParams += '&' + encodeURIComponent('endDt') + '=' + encodeURIComponent(endDate); /*조회 기간 종료일('endDate')*/
    queryParams += '&' + encodeURIComponent('stnIds') + '=' + encodeURIComponent('108'); /*서울*/

    xhr.open('GET', url + queryParams);

    xhr.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {   //요청이 성공적이라면
            // XML 응답을 파싱하기 위한 DOMParser 객체 생성
            var parser = new DOMParser();
            var xmlDoc = parser.parseFromString(this.responseText, "text/xml");

            // 사용가능한 타입으로 정보 추출 추출
            var items = xmlDoc.getElementsByTagName("item");
            var resultHTML = "";

            let avgt = 0;
            let mint = parseInt(items[0].getElementsByTagName("minTa")[0].childNodes[0].nodeValue);
            let maxt = parseInt(items[0].getElementsByTagName("maxTa")[0].childNodes[0].nodeValue);
            let avgR = 0;
            let sR = 0;
            let snow = 0;

            for (var i = 0; i < items.length; i++) {
                var avgTemp = parseInt(items[i].getElementsByTagName("avgTa")[0].childNodes[0].nodeValue);    //평균기온
                var minTemp = parseInt(items[i].getElementsByTagName("minTa")[0].childNodes[0].nodeValue);    //일 최저 기온
                var maxTemp = parseInt(items[i].getElementsByTagName("maxTa")[0].childNodes[0].nodeValue);    //일 최고 기온
                var avgRhm = parseInt(items[i].getElementsByTagName("avgRhm")[0].childNodes[0].nodeValue);    //평균 습도

                var sumRn = parseInt(items[i].getElementsByTagName("hr1MaxRn")[0]);      //일 1시간 최다 강수량

                var ddMefs = parseInt(items[i].getElementsByTagName("ddMefs")[0]);      //일 최심신적설


                avgt += avgTemp;
                mint = (mint >= minTemp) ? minTemp : mint;
                maxt = (maxt >= maxTemp) ? maxTemp : maxt;
                avgR += avgRhm;
                sR += (sumRn = '') ? (0 + sR) : (sumRn+sR);
                snow += (ddMefs = '') ? (0 + snow) : (ddMefs+snow);
            }
            avgt = avgt/(items.length);
            avgR = avgR/(items.length);
            sR = sR/(items.length);
            snow = snow/(items.length);

            // essentials 리스트를 빈 배열로 초기화
            let essentials = [];
            let memo = "";

            if (mint <= 10) {
                essentials.push(e5);
            }
            if (mint > 10 && mint < 23) {
                essentials.push(e1);
            }
            if (mint >= 23) {
                essentials.push(e3);
                if(sR < 1) {
                    essentials.push(e2);
                }
            }
            if (sR >= 1) {
                essentials.push(e4);
            }

            if (sR >= 1) {
                essentials.push(e4);
            }
            if((maxt-mint) > 9) {memo = wm1;}
            if(snow > 3) {memo = wm2;}
            if(sR > 20) {memo = wm3;}
            if(maxt > 28) {memo = wm4;}

            // essentials 배열을 콤마와 공백으로 구분된 문자열로 변환
            let essentialsList = essentials.join(', ');

            // 결과를 HTML에 표시
            resultHTML =
                "<table><tr><td colspan='2' style='color: darkblue; text-align: center'>" + lastY + "</td></tr><tr><td style='width: 100px'>"
                + tem + "</td><td>" + mint + "℃ /" + maxt + "℃  ( "+ avgt +"℃ )</td>" +
                "<tr><td>" + rhm + "</td><td>" + avgR + "%</td><tr><td>"
                + rn + "</td><td>" + sR + "(mm)</td></tr><tr><td>"
                + em + "</td><td>" + essentialsList + "</td></tr><tr><td colspan='2' style='color: green; text-align: center'>"
                + memo + "</td></tr></table>";

            document.getElementById(`weatherData`).innerHTML = resultHTML ;

        }
    };

    xhr.send();

});