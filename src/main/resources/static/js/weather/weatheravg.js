$(document).ready(function(){

    let sd = $('#sd').val();
    const startDate = sd.replace(/-/g, "") - 10000;

    let ed = $('#ed').val();
    const endDate = ed.replace(/-/g, "") - 10000;

    let lastY = $('#lastY').val();
    let tem = $('#temS').val();
    let rhm = $('#rhm').val();
    let rn = $('#avgrn').val();

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

            for (var i = 0; i < items.length; i++) {
                let dayNum = i+1;// 시작일~종료일 반복문
                var date = items[i].getElementsByTagName("tm")[0].childNodes[0].nodeValue;          //일시
                var avgTemp = items[i].getElementsByTagName("avgTa")[0].childNodes[0].nodeValue;    //평균기온
                var minTemp = items[i].getElementsByTagName("minTa")[0].childNodes[0].nodeValue;    //일 최저 기온
                var maxTemp = items[i].getElementsByTagName("maxTa")[0].childNodes[0].nodeValue;    //일 최고 기온
                var avgRhm = items[i].getElementsByTagName("avgRhm")[0].childNodes[0].nodeValue;    //평균 습도
                var sumRn = items[i].getElementsByTagName("maxTa")[0].childNodes[0].nodeValue;      //일 평균 강수량

                resultHTML = tem +
                    "<table><tr><td colspan='2'>" + lastY + "</td></tr><tr><td>"
                    + tem + "</td><td>" + minTemp + "℃ /" + maxTemp + "℃</td>" +
                    "<tr><td>" + rhm + "</td><td>" + avgRhm + "%</td><tr><td>"
                    + rn + "</td><td>" + sumRn + "(mm)</td></tr></table>";

                document.getElementById(`weatherData-${dayNum}`).innerHTML = resultHTML;

            }


            // 결과를 HTML에 표시
        }
    };

    xhr.send();

});