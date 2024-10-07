document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    const boardNum = urlParams.get('boardNum');

    const commentInput = document.querySelector('.comment-input');
    const commentsSection = document.querySelector('.comments-section');

    // 게시글 상세 정보 및 댓글 불러오기
    function loadPostDetail() {
        fetch(`/api/board/read/${boardNum}`)
            .then(response => response.json())
            .then(data => {
                // 게시글 상세 정보 표시
                document.querySelector('.user_name').textContent = data.memberName;
                document.querySelector('.write_day').textContent = `· ${new Date(data.createDate).toLocaleDateString()}`;
                document.querySelector('.Bulletin_board_text').textContent = data.contents;

                // 댓글 표시
                commentsSection.innerHTML = '';
                data.replyList.forEach(reply => {
                    const commentElement = document.createElement('div');
                    commentElement.classList.add('comment');
                    commentElement.innerHTML = `
                        <div class="comunity_text_header">
                            <img src="https://via.placeholder.com/40" alt="User_profile" />
                            <div class="row-1">
                                <strong class="user_name">${reply.memberName}</strong>
                                <span class="write_day"> · ${new Date(reply.createDate).toLocaleDateString()}</span>
                            </div>
                        </div>
                        <div class="Bulletin_board_text">${reply.contents}</div>
                    `;
                    commentsSection.appendChild(commentElement);
                });
            })
            .catch(error => console.error('Error:', error));
    }

    // 초기 로딩 시 게시글 상세 정보 및 댓글 불러오기
    loadPostDetail();

    // 댓글 작성 이벤트 처리
    commentInput.addEventListener('keypress', function (e) {
        if (e.key === 'Enter' && commentInput.value.trim() !== '') {
            e.preventDefault();

            const commentText = commentInput.value.trim();

            fetch('/api/board/replyWrite', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    // 필요 시 인증 헤더 추가
                },
                body: JSON.stringify({
                    boardNum: boardNum,
                    contents: commentText,
                    memberId: '로그인된사용자ID' // 실제로는 세션이나 토큰에서 가져옴
                })
            })
                .then(response => response.json())
                .then(data => {
                    // 댓글 목록에 새 댓글 추가
                    const commentElement = document.createElement('div');
                    commentElement.classList.add('comment');
                    commentElement.innerHTML = `
                        <div class="comunity_text_header">
                            <img src="https://via.placeholder.com/40" alt="User_profile" />
                            <div class="row-1">
                                <strong class="user_name">${data.memberName}</strong>
                                <span class="write_day"> · ${new Date(data.createDate).toLocaleDateString()}</span>
                            </div>
                        </div>
                        <div class="Bulletin_board_text">${data.contents}</div>
                    `;
                    commentsSection.appendChild(commentElement);

                    // 댓글 입력란 초기화
                    commentInput.value = '';
                })
                .catch(error => console.error('Error:', error));
        }
    });

    // 좋아요 버튼 클릭 이벤트 처리 (상세 페이지에서)
    document.body.addEventListener('click', function (event) {
        const target = event.target;

        if (target.closest('.good-button')) {
            event.stopPropagation();
            const goodButton = target.closest('.good-button');

            fetch('/api/board/like', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    // 필요 시 인증 헤더 추가
                },
                body: JSON.stringify({
                    boardNum: boardNum
                })
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        goodButton.querySelector('.good-num').textContent = data.likeCount;
                        goodButton.dataset.liked = 'true';
                    } else {

                    }
                })
                .catch(error => console.error('Error:', error));
        }
    });
});
