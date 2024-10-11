$(document).ready(function() {
    const $spanKor = $(".span_kor");
    const $spanJp = $(".span_jp");

    // 한국어 버튼 클릭 이벤트
    $spanKor.click(function() {
        $spanKor.css("font-weight", "bold");
        $spanJp.css("font-weight", "normal");
        changelang("ko")
    });

    // 일본어 버튼 클릭 이벤트
    $spanJp.click(function() {
        $spanJp.css("font-weight", "bold");
        $spanKor.css("font-weight", "normal");
        changelang("ja");
    });

});

function changelang(lang) { // 선택된 언어 값 (ko 또는 ja)

    $.ajax({
        url: 'locale',  // 서버에서 처리할 경로
        type: 'GET',      // GET 방식으로 요청
        data: {lang: lang},  // 전송할 데이터
        success: function () {
            location.reload();
        },
        error: function (e) {
            /*                location.reload();*/
            console.error("언어변경 과정에 문제 있음");
            //console.log(JSON.stringify(e));
        }
    });
}