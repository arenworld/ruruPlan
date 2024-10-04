$(document).ready(function(){
    // 모달 요소
    var termsModal = document.getElementById("terms");

    $('#termsLink').on('click', function (event) {
        event.preventDefault();
        termsModal.style.display = "block";
    });

    // 모달 닫기
    $('.close').on('click', function() {
        termsModal.style.display = "none";
    })

    // 모달 외부 클릭 시 닫기
    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }

    //비밀번호 노출,숨기기 기능
    $('.input-group i').on('click', function () {
        $('#memberPw').toggleClass('active');
        if ($('input').hasClass('active')) {
            $(this).attr('class', "fa-solid fa-eye")
                .prev('input').attr('type', "text");
        } else {
            $(this).attr('class', "fa-solid fa-eye")
                .prev('input').attr('type', 'password');
        }
    });
    //모달창 열기
    $('.agree').on('click', function (event) {
        event.preventDefault();
        $('.terms').addClass('open'); // terms 모달 창 열기
        $('.modal-backdrop').addClass('open'); // 배경 활성화
    });

    // 모달 닫기
    $('.modal-backdrop').on('click', function () {
        $('.terms').removeClass('open'); // terms 모달 창 닫기
        $('.modal-backdrop').removeClass('open'); // 배경 비활성화
    });

    $("#sharePlan").click(share);
});

function share(){

}