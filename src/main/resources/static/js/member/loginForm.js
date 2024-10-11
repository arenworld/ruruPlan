$(document).ready(function(){
    // 로그인 폼 초기화
    init();

    // 쿠키 저장
    $('.login-form').submit(function (){
        let userInfo = $(this).serializeObject();

        axios.post('/member/login', userInfo)
            .then(response => {
                //console.log(response);
                if($('#remember_id').prop("checked")){  // 쿠키에 ID 저장
                    setCookie("rememberId", $("#memberId").val(), 10) // 10일간 ID 저장
                } else { // 체크 해제 쿠키에서 값 삭제
                    deleteCookie("rememberId");
                }
                window.location.href = '/';
            })
            .catch(error => {
                alert(error.response.data);
            });
        return false;
    });

    // 쿠키 설정
    function setCookie(name, value, expiredays){
        const date = new Date();
        date.setDate(date.getDate() + expiredays);
        document.cookie = encodeURIComponent(name) + "=" + encodeURIComponent(value) + "; path=/; expires=" + date.toUTCString();
    }

    // 쿠키 삭제
    function deleteCookie(name) {
        const expireDate = new Date();
        expireDate.setDate(expireDate.getDate() - 1);
        document.cookie = name + "=" + "; expires=" + expireDate.toUTCString();
    }

    // 로그인 폼 초기화
    function init(){
        const rememberId = getCookie("rememberId");
        // 쿠키에 아이디가 있을 경우
        if(rememberId != "" && typeof(rememberId) != "undefined") {
            $('#remember_id').prop("checked",true);
            $('#memberId').val(rememberId);
        } else {    //저장된 아이디가 없을 경우
            $('#remember_id').prop("checked",false);
        }
    }

    // 쿠키 조회
    function getCookie(name) {
        const cookie = document.cookie;
        if (cookie !== "") {
            var cookieArray = cookie.split("; ");
            for (var index in cookieArray) {
                var cookieName = cookieArray[index].split("=");
                if (cookieName[0] == name) {
                    return decodeURIComponent(cookieName[1]);
                }
            }
        }
        return "";
    }

    // serializeObject 함수 추가 (필요한 경우)
    $.fn.serializeObject = function() {
        var obj = {};
        var arr = this.serializeArray();
        $.each(arr, function() {
            if (obj[this.name]) {
                if (!obj[this.name].push) {
                    obj[this.name] = [obj[this.name]];
                }
                obj[this.name].push(this.value || '');
            } else {
                obj[this.name] = this.value || '';
            }
        });
        return obj;
    };
});
