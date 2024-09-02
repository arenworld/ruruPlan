$(document).ready(function() {
    // 아이디 중복확인
    $(document).ready(function() {

        $('#memberId').keyup(function() {
            let id = $(this).val();
            if (id.length < 6 || id.length > 15) {
                $('#msg').css('color', 'red');
                $('#msg').html('아이디를 6~15글자로 입력해 주세요');
            }
        });
        $('#idCertification').click(function(){
            let id = $('#memberId').val();
            $.ajax({
                url: 'idDuplicate',
                type: 'post',
                data: {id : id},
                success: function(res){
                    if(res){
                        $('#msg').css('color', 'red');
                        $('#msg').html("이미 사용중인 ID입니다");
                    } else{
                        $('#msg').css('color', 'blue');
                        $('#msg').html("사용가능한 ID입니다.");
                    }
                },
                error: function(){
                    alert('에러');
                }

            });
        });
        });

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