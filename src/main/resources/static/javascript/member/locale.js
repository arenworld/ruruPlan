$(document).ready(function() {
    $('#lang').on('change', function () {
        var selectedLang = $(this).val();  // 선택된 언어 값 (ko 또는 ja)

        $.ajax({
            url: 'locale',  // 서버에서 처리할 경로
            type: 'GET',      // GET 방식으로 요청
            data: {lang: selectedLang},  // 전송할 데이터
            success: function () {
                location.reload();
            },
            error: function () {
/*                location.reload();*/
                console.error("언어변경 과정에 문제 있음");
            }
        });
    });
});
