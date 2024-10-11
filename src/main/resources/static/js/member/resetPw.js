$(document).ready(function(){
    // 비밀번호 유효성 검사
    $('#memberPw').keyup(validatePw);
    // 비밀번호 대조,확인
    $('#pwConfirm').keyup(pwConfirm);

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

    //폼제출 전 유효성 검사
    $('#submit').on('click', function(){
        if(!validatePw()) {
            alert("유효하지 않은 비밀번호입니다.");
            //console.log(validatePw());
            return false;
        }
        if (!pwConfirm()) {
            alert("비밀번호가 일치하지 않습니다.");
            return false;
        }
        return true;
    });
});

// 비밀번호 유효성 검사
function validatePw() {
    //비밀번호 정규표현식(영문/숫자/특수문자를 1자 이상 포함 8~15자, 대소문자 구분)
    pw_regex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[~!@#$%^&*_\-+=`|\\(){}[\]:;"'<>,.?/])[a-zA-Z\d~!@#$%^&*_\-+=`|\\(){}[\]:;"'<>,.?/]{8,15}$/;
    let pw = $('#memberPw').val();

    if (!pw_regex.test(pw)) {
        $('#pwmsg1').css('color', 'red');
        $('#pwmsg1').css('font-size', '12px');
        $('#pwmsg1').html('영문/숫자/특수문자를 1자 이상 포함한 8~15자로 입력');
        return false;
    } else {
        $('#pwmsg1').html("");
        return true;
    }
}

// 비밀번호 일치 확인
function pwConfirm(){
    let pwConfirm = $('#pwConfirm').val();
    let pw = $('#memberPw').val();

    if(pwConfirm == pw){
        $('#pwmsg').css('color', 'green');
        $('#pwmsg').html("비밀번호 일치");
        return true;
    } else {
        $('#pwmsg').css('color', 'red');
        $('#pwmsg').html("비밀번호가 일치하지 않습니다.");
        return false;
    }
}