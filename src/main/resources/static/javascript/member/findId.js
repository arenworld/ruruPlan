$(document).ready(function() {
    //이메일 인증번호
    $('#emCodeBt').click(emConfirm);
});

// 이메일 인증 번호 전송
function emConfirm(){
    let email = $('#email').val();
    $('#email').prop('disabled', true);
    $('#emCodeBt').prop('disabled', true);

    $.ajax({
        url: '/join/mailConfirm',
        type: 'post',
        data: {email: email},
        success: function (code) {
            if (code) {
                alert("이메일이 전송되었습니다." +
                    "인증번호 확인 후 입력해주세요.");
                chkEmailConfirm(code);
            }
        },
        error: function () {
            alert("이메일 전송에 실패하였습니다.");
        }
    });
}

function chkEmailConfirm(code){
    $('#emailconfirm').on("keyup", function(){
        if (code != $('#emailconfirm').val()) {
            emconfirmchk = false;
            $('#emchkmsg').html("<span id='emconfirmchk'>인증번호가 잘못되었습니다</span>")
            $("#emconfirmchk").css({
                    "color" : "#FA3E3E",
                    "font-weight" : "bold",
                    "font-size" : "10px"
                }
            )
            return emconfirmchk;
        } else {
            emconfirmchk = true;
            $('#emailconfirm').prop('disabled', true);
            $('#findId').prop('disabled', false);
            $('#emchkmsg').html("<span id='emconfirmchk'>인증번호 확인 완료</span>")
            $("#emconfirmchk").css({
                "color" : "#0D6EFD",
                "font-weight" : "bold",
                "font-size" : "10px"
            });
            return emconfirmchk;
        }
    });
}