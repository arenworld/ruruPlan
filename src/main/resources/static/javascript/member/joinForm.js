$(document).ready(function() {
    //비밀번호 노출,숨기기 기능
    $('.input-group i').on('click',function(){
        $('#memberPw').toggleClass('active');
        if($('input').hasClass('active')){
            $(this).attr('class', "fa-solid fa-eye")
                .prev('input').attr('type', "text");
        }else{
            $(this).attr('class',"fa-solid fa-eye")
                .prev('input').attr('type','password');
        }
    });
    //모달창 열기
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

    // 아이디 유효성 검사
    $('#memberId').keyup(idConfirm);
    // 닉네임
    $('#nickname').keyup(nicknameConfirm);
    // 비밀번호 유효성 검사
    $('#memberPw').keyup(validatePw);
    // 비밀번호 대조,확인
    $('#pwConfirm').keyup(pwConfirm);
    //이메일 형식/ 중복 검사
    $('#email').keyup(validateEmail);
    //이메일 인증번호
    $('#emCodeBt').click(emConfirm);

    //회원가입 폼제출시 검사
   $('#joinForm').submit(formConfirm);

});

    // 아이디 유효성 겁사
    function idConfirm(){
    //정규식   (문자열, 영어(필수)+숫자(선택) 6~15자, 대소문자 구분)
    id_regex = /^(?=.*[a-zA-Z])[a-zA-Z0-9]{6,15}$/;
    let id = $('#memberId').val();

    /*아이디 형식 확인*/
    if (!id_regex.test(id)) {
    $('#idmsg').css('color', 'red');
    $('#idmsg').html('아이디는 영문,숫자로 6~15글자 입력해 주세요');
    return false;
} else {
    /*아이디 중복확인*/
    $.ajax({
    url: 'idDuplicate',
    type: 'post',
    data: {id: id},
    success: function (res) {
    if (res) {
    $('#idmsg').css('color', 'red');
    $('#idmsg').html("이미 사용중인 ID입니다");
    return false;
} else {
    $('#idmsg').css('color', 'blue');
    $('#idmsg').html("사용가능한 ID입니다.");
    return true;
}
},
    error: function () {
    alert('IdDuplicate error');
    return false;
}
});
    return valid;
}
}

    // 닉네임 유효성 검사
    function nicknameConfirm(){
    //정규식 (영문/항글/숫자 2~10자, 대소문자 구분)
    nickname_regex = /^[A-Za-z가-힣0-9]{2,10}$/;
    let nickname = $('#nickname').val();

    if (!nickname_regex.test(nickname)) {
    $('#nicknamemsg').css('color', 'red');
    $('#nicknamemsg').html('닉네임은 영문,한글,숫자로 1~10글자 입력해 주세요');
    return false;
} else {
    $('#nicknamemsg').css('color', 'blue');
    $('#nicknamemsg').html('사용 가능한 닉네임입니다.');
    return true;
}
}

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

    //이메일 유효성 검사 (형식+중복확인)
    function validateEmail() {
    //이메일 정규표현식
    email_regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/i;
    let email = $('#email').val();

    if (!email_regex.test(email)) {
    $('#emailmsg').css('color', 'red');
    $('#emailmsg').html("유효하지 않은 이메일 주소입니다.");
    $('#emCodeBt').prop('disabled', true);
    return false;
} else {
    // 중복 검사
    let valid = false;
    $.ajax({
    url: 'emailDuplicate',
    type: 'post',
    data: {email: email},
    success: function (res) {
    if (res) {
    $('#emailmsg').css('color', 'red');
    $('#emailmsg').html("이미 사용중인 email입니다");
    $('#emCodeBt').prop('disabled', true);
    valid = false;
} else {
    $('#emailmsg').css('color', 'black');
    $('#emailmsg').html("사용 가능");
    $('#emCodeBt').prop('disabled', false);
    valid = true;
}
},
    error: function () {
    alert('EmailDuplicate error');
    valid = false;
}
});
    return valid;
}

}

    // 이메일 인증 번호 전송
    function emConfirm(){
    let email = $('#email').val();

    $.ajax({
    url: 'mailConfirm',
    type: 'post',
    data: {email: email},
    success: function (code) {
    if (code) {
    alert("이메일이 전송되었습니다." +
    "인증번호 확인 후 입력해주세요.")
    chkEmailConfirm(code);
}
},
    error: function () {
    alert("이메일 전송에 실패하였습니다.")
}
});
}
    // 이메일 인증 번호 확인
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
            $('#emchkmsg').html("<span id='emconfirmchk'>인증번호 확인 완료</span>")
            $("#emconfirmchk").css({
                "color" : "#0D6EFD",
                "font-weight" : "bold",
                "font-size" : "10px"
            })
            return emconfirmchk;
        }
    })
}

    //회원가입 폼 제출 내용 확인
function formConfirm(){
        alert("실행은 일단 됨");
    // 아이디 유효성 검사
    if(!idConfirm()) {
        alert("유효하지 않은 아이디입니다.");
        console.log(idConfirm());
        return false;
    }
    if(!validatePw()) {
        alert("유효하지 않은 비밍번호입니다.");
        return false;
    }
    if (!pwConfirm()) {
        alert("비밀번호가 일치하지 않습니다.");
        return false;
    }
    if(!nicknameConfirm()) {
        alert("유효하지 않은 닉네임입니다.");
        return false;
    }
    if (!validateEmail()) {
        alert("유효하지 않은 이메일입니다.");
        return false;
    }
    if(!chkEmailConfirm()) {
        alert("인증번호가 일치하지 않습니다.")
    }
    return true;
}
