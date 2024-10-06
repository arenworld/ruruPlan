$(document).ready(function(){

    $('.sharePlan').on('click', function(){
        const planNum = $(this).attr('data-plan-num');
        const msg = $('#shareMsg').val();
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