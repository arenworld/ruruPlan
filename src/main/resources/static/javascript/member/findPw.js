$(document).ready(function() {
   //초기 설정: Em확인 부분 숨김
    $('#confirmEm').hide();
    $('#confirmCode').hide();
    //아이디 존재 여부 확인
    $('#IdConBt').click(idConfirm);
    //이메일 형식 및 일치 검사
    $('#email').keyup(validateEmail);
    //이메일 인증번호
    $('#emCodeBt').click(emConfirm);
});

//아이디 존재 여부 확인
function idConfirm() {
    let id = $('#memberId').val();

    $.ajax({
        url: '/login/idConfirm',
        type: 'post',
        data: {id: id},
        success: function (res) {
            $('#confirmId').hide();   // ConfirmId div 숨기기
            $('#confirmEm').show();   // ConfirmEm div 보여주기
            $('#getEmail').val(res);
        },
        error: function () {
            alert("입력하신 아이디를 찾을 수 없습니다.");
        }
    });
}

//이메일 유효성 검사 (형식+일치확인)
function validateEmail() {
    //이메일 정규표현식
    email_regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/i;
    let email = $('#email').val();
    let getEmail = $('#getEmail').val();
    if (!email_regex.test(email) || email !== getEmail) {
        $('#emailmsg').css('color', 'red');
        $('#emailmsg').html("이메일이 일치하지 않습니다.");
        $('#emCodeBt').prop('disabled', true);
        return Promise.resolve(false);
    }
    $('#emailmsg').html("");
    $('#emCodeBt').prop('disabled', false);
}

// 이메일 인증 번호 전송
function emConfirm(){
    let email = $('#email').val();
    $("#email").prop("readonly", true);
    $('#emCodeBt').prop('disabled', true);

    $('#confirmEm').hide();
    $('#confirmCode').show();

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

//인증번호 일치 확인
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
            $('#findPw').prop('disabled', false);
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

