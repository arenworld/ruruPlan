$(document).ready(function(){
    $('.button.-regular').click(function(){
        const btn = $(this);

        const bgColor = btn.css('background-color');
        if(bgColor === 'rgb(225, 226, 226)'){
            cancel(btn);
        } else {
            search(btn);        }
    });
});

function search(btn){
    const text = btn.text();
    btn.css('background-color', '#e1e2e2');
}

function cancel(btn){
    const text = btn.text();
    btn.css('background-color', '#f2f2f2');
}