$(document).ready(function() {
        // 연결 안됨...
    // 모달 열기
    $('.agree').on('click', function(event) {
        event.preventDefault();
        $('.terms').addClass('open'); // terms 모달 창 열기
        $('.modal-backdrop').addClass('open'); // 배경 활성화
    });

    // 모달 닫기
    $('.modal-backdrop').on('click', function() {
        $('.terms').removeClass('open'); // terms 모달 창 닫기
        $('.modal-backdrop').removeClass('open'); // 배경 비활성화
    });
});