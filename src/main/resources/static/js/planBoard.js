$(document).ready(function() {

    loadPlanBoardList(1); // 초기 로드 시 첫 페이지를 불러옴

    // 동적으로 추가된 요소에 이벤트 바인딩
    $(document).on('click', '.save', function() {
        const planNum = $(this).attr('data-board-num');
        const msg = $('#shareMsg').val();
        $.ajax({
            url: 'savePlan',
            type: 'post',
            data: { planNum: planNum },
            success: function() {
                alert(msg);
            },
            error: function() {
                alert('Sharing error');
            }
        });
    });
});

function loadPlanBoardList(page) {
    $.ajax({
        url: '/planBoard/listJson', // API URL
        type: 'GET',
        dataType: 'json',
        data: {
            page: page,
            tag1: '', // 필요한 태그 필터링 값들
            tag2: '',
            tag3: '',
            tag4: '',
            tag5: '',
            tag6: ''
        },
        success: function(response) {
            renderPlanBoardList(response.content); // 성공 시 HTML 그리기
        },
        error: function(error) {
            console.error("Error fetching plan board list", error);
        }
    });
}

function renderPlanBoardList(boardList) {
    let boardContainer = $('#plan-board-list'); // 플랜 리스트가 들어갈 정확한 컨테이너 선택
    boardContainer.empty(); // 기존 내용을 지움

    // 받아온 boardList로 HTML 동적 생성
    boardList.forEach(function(board) {
        let boardHtml = `
            <div class="col-6 col-sm-6 col-md-6 col-lg-3 mb-4 mb-lg-0">
                <div class="media-1">
                    <a href="#" class="d-block mb-3">
                        <img src="${board.coverImageUrl}" alt="Image" class="img-fluid">
                    </a>
                </div>
                <span class="d-flex align-items-center loc mb-2">
                    <div>
                        <img id="like" src="/images/plan/하트.png" style="width: 25px; height: 25px;">
                        <span>${board.likeCount}</span>
                        <img id="save" src="/images/plan/북마크.png" style="width: 30px; height: 30px;">
                    </div>
                </span>
                <div class="d-flex align-items-center">
                    <div>
                        <h3><a href="#">${board.planName}</a></h3>
                        <div class="price ml-auto" style="border-bottom: 100px">
                            <span>#${board.tag1}</span>
                            <span>#${board.tag2}</span>
                            <span>#${board.tag3}</span>
                            <span>#${board.tag4}</span>
                            ${board.tag5 ? `<span>#${board.tag5}</span>` : ''}
                            ${board.tag6 ? `<span>#${board.tag6}</span>` : ''}
                        </div>
                    </div>
                </div>
            </div>
        `;
        boardContainer.append(boardHtml); // 컨테이너에 추가
    });
}
