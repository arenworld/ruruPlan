$(document).ready(function() {
    // select 요소를 선택
    var selectElement = $('#age');
    // userAge에 해당하는 option을 기본값으로 설정
    userAge = $('#userAge').text();
    selectElement.val(userAge);

    $('#confirm').on('click' ,function(){
        location.href='/';
    });
    // 비밀번호 유효성 검사
    $('#memberPw').keyup(confirmpw);
    // 비밀번호 대조,확인
    $('#pwConfirm').keyup(confirmpw);

});
 let lang = $('#lang').val();
function confirmpw(){
    //비밀번호 일치 확인
    let pw = $('#memberPw').val();
    let pwConfirm = $('#pwConfirm').val();
        if(pwConfirm == pw){
            $('#pwmsg').css('color', 'green');
            $('#pwmsg').html(lang === 'ko' ? "비밀번호 일치" : 'パスワード一致');
        } else {
            $('#pwmsg').css('color', 'red');
            $('#pwmsg').html(lang === 'ko' ? "비밀번호가 일치하지 않습니다" : 'パスワードが一致しません');
        }
}

    function validateForm(){

        //닉네임 정규식 (영문/한글/일본어/숫자 2~10자, 대소문자 구분)
        nickname_regex = /^[A-Za-z가-힣ぁ-ゔァ-ヴ一-龥0-9]{2,10}$/;
        let nickname = $('#nickname').val();
        //비밀번호 정규표현식(영문/숫자/특수문자를 1자 이상 포함 8~15자, 대소문자 구분)
        pw_regex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[~!@#$%^&*_\-+=`|\\(){}[\]:;"'<>,.?/])[a-zA-Z\d~!@#$%^&*_\-+=`|\\(){}[\]:;"'<>,.?/]{8,15}$/;
        var pw = $('#memberPw').val();
        var pwConfirm = $('#pwConfirm').val();

        // 비밀번호 일치
        if(pwConfirm != pw){
            return false; }
        // 닉네임 유효성 검사
        if (nickname == "" || nickname === null) {
        }else if (!nickname_regex.test(nickname)) {
            var msg = lang === 'ko' ? '닉네임은 영문,한글,일어,숫자로 2~10 입력해 주세요' : 'ニックネームは英文、ハングル、日本語、数字で2文字以上、10文字以内でご入力ください';
            alert(msg);
            return false;
        }
        //비밀번호 유효성 검사
        if (pw == "" || pw === null){
        }else if (!pw_regex.test(pw)) {
            var msg =lang === 'ko' ? '영문/숫자/특수문자를 1자 이상 포함한 8~15자로 입력해주세요' : '英数字特殊文字を1文字以上含む8文字以上、15文字以内でご入力ください';
            alert(msg);
            return false;
        }
        return true;
    }


