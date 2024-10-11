
$(document).ready(function() {
    lang = $('#lang').val();
    //초기 설정: Id 확정 부분 숨김
    $('#finalId').hide();

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
    $('#emCodeReBt').click(emConfirm);

    $('#finalId').click(finalId);

    //회원가입 폼제출시 검사
    $('#submit').click(formConfirm);

});

// 아이디 유효성 겁사
function idConfirm() {
    //정규식   (문자열, 영어(필수)+숫자(선택) 6~15자, 대소문자 구분)
    id_regex = /^(?=.*[a-zA-Z])[a-zA-Z0-9]{6,15}$/;
    let id = $('#memberId').val();

    /*아이디 형식 확인*/
    if (!id_regex.test(id)) {
        $('#finalId').hide();   // 아이디 확정 버튼 숨기기
        $('#idmsg').css('color', 'red');
        $('#idmsg').html(lang === 'ko' ? '아이디는 영문,숫자로 6~15글자 입력해 주세요' : 'IDは英数字で6文字以上、15文字以内でご入力ください');
        return Promise.resolve(false);  // 형식이 잘못되었으면 바로 false 반환
    }

    /*아이디 중복확인*/
    //console.log("return " + idDuplicated());
    return idDuplicated();
}

// 아이디 중복확인
function idDuplicated() {
    return new Promise((resolve, reject) => {
        let id = $('#memberId').val();

        $.ajax({
            url: 'idDuplicate',
            type: 'post',
            data: {id: id},
            success: function (res) {
                if (res) {
                    $('#finalId').hide();
                    $('#idmsg').css('color', 'red');
                    $('#idmsg').html(lang === 'ko' ? "이미 사용중인 ID입니다" : '既に使用中のIDです');
                    //console.log("에이젝스 flase");
                    resolve(false);  // 중복된 ID이면 false 반환
                } else {
                    $('#finalId').show();
                    $('#idmsg').html("");
                    //console.log("에이젝스 true");
                    resolve(true);  // 사용 가능한 ID이면 true 반환
                }
            },
            error: function () {
                alert('IdDuplicate error');
                //console.log("에이젝스 error");
                resolve(false);  // 에러 발생 시 false 반환
            }
        });
    });
}

// 아이디 확정
function finalId() {
    $("#memberId").prop("readonly", true);
    $("#memberId").css('backgroundColor', 'gray');
}

// 닉네임 유효성 검사
function nicknameConfirm(){
    //정규식 (영문/한글/일본어/숫자 2~10자, 대소문자 구분)
    nickname_regex = /^[A-Za-z가-힣ぁ-ゔァ-ヴ一-龥0-9]{2,10}$/;
    let nickname = $('#nickname').val();

    if (!nickname_regex.test(nickname)) {
        $('#nicknamemsg').css('color', 'red');
        $('#nicknamemsg').html(lang === 'ko' ? '닉네임은 영문,한글,일어,숫자로 2~10 입력해 주세요' : 'ニックネームは英文、ハングル、日本語、数字で2文字以上、10文字以内でご入力ください');
        return false;
    } else {
        $('#nicknamemsg').css('color', 'blue');
        $('#nicknamemsg').html(lang === 'ko' ? '사용 가능한 닉네임입니다' : '使用可能なニックネームです');
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
        $('#pwmsg1').html(lang === 'ko' ? '영문/숫자/특수문자를 1자 이상 포함한 8~15자로 입력해주세요' : '英数字特殊文字を1文字以上含む8文字以上、15文字以内でご入力ください');
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
        $('#pwmsg').html(lang === 'ko' ? "비밀번호 일치" : 'パスワード一致');
        return true;
    } else {
        $('#pwmsg').css('color', 'red');
        $('#pwmsg').html(lang === 'ko' ? "비밀번호가 일치하지 않습니다" : 'パスワードが一致しません');
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
        $('#emailmsg').html(lang === 'ko' ? "유효하지 않은 이메일 주소 입니다" : "無効なメールアドレスです");
        $('#emCodeBt').prop('disabled', true);
        return Promise.resolve(false);
    }
    // 중복 검사
    //console.log("return " + emailDuplicate());
    return emailDuplicate();
}

// 이메일 중복 검사
function emailDuplicate() {
    return new Promise((resolve, reject) => {

        let email = $('#email').val();

        $.ajax({
            url: 'emailDuplicate',
            type: 'post',
            data: {email: email},
            success: function (res) {
                if (res) {
                    $('#emailmsg').css('color', 'red');
                    $('#emailmsg').html(lang === 'ko' ? "이미 사용중인 이메일 주소 입니다" : '既に使用中のメールアドレスです');
                    $('#emCodeBt').prop('disabled', true);
                    resolve(false);
                } else {
                    $('#emailmsg').html("");
                    $('#emCodeBt').prop('disabled', false);
                    resolve(true);
                }
            },
            error: function () {
                alert('EmailDuplicate error');
                resolve(false);
            }
        });
    });
}

// 이메일 인증 번호 전송
function emConfirm(){
    let email = $('#email').val();

    $("#email").prop("readonly", true);
    $('#emCodeBt').prop('disabled', true);
    $('#emCodeReBt').prop('disabled', false);

    $.ajax({
        url: 'mailConfirm',
        type: 'post',
        data: {email: email},
        success: function (code) {
            if (code) {
                alert(lang === 'ko' ? "메일이 전송되었습니다" +
                "인증번호 확인 후 입력해주세요" : "メールが送信されました" +
                "認証番号をご確認の上、ご入力ください");
                $('#emailconfirm').prop('readonly',false);
                chkEmailConfirm(code);
            }
        },
        error: function () {
            alert(lang === 'ko' ? "메일 전송에 실패하였습니다" : "メールの送信に失敗しました");
        }
    });
}
// 이메일 인증 번호 확인
function chkEmailConfirm(code){
    $('#emailconfirm').on("keyup", function(){
        if (code != $('#emailconfirm').val()) {
            $('#emchkmsg').html(lang === 'ko' ? "<span id='emconfirmchk'>인증번호가 잘못되었습니다</span>"
            : "<span id='emconfirmchk'>認証番号が正しくありません</span>")
            $("#emconfirmchk").css({
                    "color" : "#FA3E3E",
                    "font-weight" : "bold",
                    "font-size" : "10px"
                }
            )
        } else {
            $('#emailconfirm').prop('readonly',true);
            $('#emchkmsg').html(lang === 'ko' ? "<span id='emconfirmchk'>인증번호 확인 완료</span>"
            : "<span id='emconfirmchk'>認証番号確認完了</span>")
            $("#emconfirmchk").css({
                "color" : "#0D6EFD",
                "font-weight" : "bold",
                "font-size" : "10px"
            });
        }
    });
}

//회원가입 폼 제출 내용 확인
function formConfirm(){

    if(!validatePw()) {
        alert(lang === 'ko' ? "유효하지 않은 비밀번호입니다" : "無効なパスワードです");
        $('#memberPw').css('border-color', red);
        return false;
    }

    if (!pwConfirm()) {
        alert(lang === 'ko' ? "비밀번호가 일치하지 않습니다" : "パスワードが一致しません");
        return false;
    }

    if(!nicknameConfirm()) {
        alert(lang === 'ko' ? "유효하지 않은 닉네임입니다" : "無効なニックネームです");
        return false;
    }

    if(!$('#emailconfirm').prop('readonly') ) {
        alert(lang === 'ko' ? "인증번호가 일치하지 않습니다" : "認証番号が一致しません");
        return false;
    }

    if(!$('#memberId').prop('readonly')) {
        alert(lang === 'ko' ? "아이디가 확정되지 않았습니다" : "IDが確定されていません")
        return false;
    }

    if(!$('#email').prop('readonly')) {
        alert(lang === 'ko' ? "유효하지 않은 이메일입니다" : "無効なメールアドレスです")
        return false;
    }

    alert(lang === 'ko' ? '환영합니다.' : '歓迎致します.');
}
