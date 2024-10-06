$(document).ready(function(){

    $('.save').on('click', function(){
        const planNum = $(this).attr('data-board-num');
        const msg = $('#shareMsg').val();
        $.ajax({
            url: 'savePlan ',
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