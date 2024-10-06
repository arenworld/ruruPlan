$(document).ready(function(){

    $('.sharePlan').on('click', function(){
        const planNum = $(this).attr('data-plan-num');
        const msg = $('#shareMsg').text();
        console.log("나 찍히긴 했어 {}", planNum);
        console.log(msg);
        $.ajax({
            url: 'planShare',
            type: 'post',
            data: {planNum: planNum},
            success: function () {
                alert(msg);
            },
            error: function () {
                alert('Sharing error');
            }
        });
    });
});