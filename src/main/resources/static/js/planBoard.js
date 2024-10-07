$(document).ready(function() {
    let selectedTags = []; // 선택된 태그들을 저장하는 배열
    let currentPage = 1;   // 현재 페이지 번호
    const pageSize = 12;   // 한 페이지에 표시할 게시물 수
    let lang = $('#lang').val();

    i18next.init({
        lng: $('#lang').val(), // 'ko' 또는 'ja'
        resources: {
            ko: {
                translation: {
                    "spring": "봄",
                    "summer": "여름",
                    "fall": "가을",
                    "winter": "겨울",
                    "2days": "1박2일",
                    "3days": "2박3일",
                    "4days": "3박4일",
                    "5days": "4박5일",
                    "6days": "5박6일",
                    "trip_type1": "혼자",
                    "trip_type2": "커플",
                    "trip_type3": "부모님",
                    "trip_type4": "친구",
                    "kids": "아이",
                    "theme1": "쇼핑",
                    "theme2": "음식",
                    "theme3": "카페",
                    "theme4": "역사",
                    "theme5": "문화",
                    "theme6": "힐링",
                    "theme7": "체험",
                    "theme8": "랜드마크",
                    "theme9_1": "수상레저",
                    "theme9_2": "아이스링크",
                    "theme9_3": "자전거"
                }
            },
            ja: {
                translation: {
                    "spring": "春",
                    "summer": "夏",
                    "fall": "秋",
                    "winter": "冬",
                    "2days": "1泊2日",
                    "3days": "2泊3日",
                    "4days": "3泊4日",
                    "5days": "4泊5日",
                    "6days": "5泊6日",
                    "trip_type1": "一人",
                    "trip_type2": "カップル",
                    "trip_type3": "ご両親",
                    "trip_type4": "友達",
                    "kids": "お子さま",
                    "theme1": "ショッピング",
                    "theme2": "食べ物",
                    "theme3": "カフェ",
                    "theme4": "歴史",
                    "theme5": "文化",
                    "theme6": "ヒーリング",
                    "theme7": "体験",
                    "theme8": "ランドマーク",
                    "theme9_1": "ウォータースポーツ",
                    "theme9_2": "アイスリンク",
                    "theme9_3": "自転車"
                }
            }
        }
    }, function(err, t) {
        // 초기화 완료 후 필요한 작업 수행
    });

    // 버튼 클릭 이벤트 처리
    $('.button-container .button').on('click', function() {
        const tag = $(this).data('tag'); // data-tag 속성에서 태그 값 가져오기

        // 선택된 태그 배열에 해당 태그가 있는지 확인
        const index = selectedTags.indexOf(tag);
        if (index === -1) {
            // 태그가 없으면 추가
            selectedTags.push(tag);
            $(this).addClass('selected'); // 선택된 버튼에 표시 (배경색 변경 등)
        } else {
            // 이미 선택된 태그면 배열에서 제거
            selectedTags.splice(index, 1);
            $(this).removeClass('selected'); // 선택 해제 표시
        }

        // 태그가 변경될 때마다 목록을 업데이트
        currentPage = 1; // 페이지 초기화
        loadPlanBoardList(currentPage, selectedTags); // 1페이지부터 로드
    });

    // 페이지네이션 버튼 클릭 이벤트
    $(document).on('click', '.pagination .page-link', function(e) {
        e.preventDefault();
        const target = $(this).attr('aria-label');

        if (target === 'Previous') {
            if (currentPage > 1) {
                currentPage--;
                loadPlanBoardList(currentPage, selectedTags);
            }
        } else if (target === 'Next') {
            currentPage++;
            loadPlanBoardList(currentPage, selectedTags);
        } else {
            const page = parseInt($(this).text());
            if (!isNaN(page)) {
                currentPage = page;
                loadPlanBoardList(currentPage, selectedTags);
            }
        }
    });

    // 처음 페이지 로드 시 모든 태그를 선택하지 않고 기본적으로 실행
    loadPlanBoardList(currentPage, selectedTags);

    // 하트 버튼 클릭 이벤트 처리
    $(document).on('click', '.like', function() {
        const boardNum = $(this).attr('data-board-num');
        let likeCount = "like-" + boardNum;
        const msg = $('#shareMsg').val();
        $.ajax({
            url: 'likePlan',
            type: 'post',
            data: { boardNum: boardNum },
            success: function(res) {
                if (res == '') {
                    alert(lang === 'ko' ? '자신의 게시물에는 좋아요를 할 수 없습니다.' : '自分の投稿にはいいねができません。');
                } else {
                    document.getElementById(likeCount).innerHTML = res ;
                }

            },
            error: function() {
                alert(lang !== 'ko' ? '오류 발생' : '失敗しました!');
            }
        });
    });

    // Save 버튼 클릭 이벤트 처리
    $(document).on('click', '.save', function() {
        const boardNum = $(this).attr('data-board-num');
        const msg = $('#shareMsg').val();
        $.ajax({
            url: 'savePlan',
            type: 'post',
            data: { boardNum: boardNum },
            success: function() {
                alert(lang === 'ko' ? '일정 북마크 성공' : 'ブックマーク、成功!');
            },
            error: function() {
                alert(lang !== 'ko' ? '저장 중 오류 발생' : '保存に失敗しました!');
            }
        });
    });
});
// 플랜 게시판 목록 로드 함수
function loadPlanBoardList(page, tags) {
    $.ajax({
        url: '/planBoard/listJson', // API URL
        type: 'GET',
        dataType: 'json',
        traditional: true, // 배열 데이터를 제대로 전달하기 위해 설정
        data: {
            page: page,
            tags: tags // 선택된 태그 배열을 서버에 전달
        },
        success: function(response) {
            renderPlanBoardList(response.content); // 데이터 렌더링
            renderPagination(response);            // 페이지네이션 렌더링 추가
        },
        error: function(error) {
            console.error("플랜 목록을 불러오는 중 오류 발생", error);
        }
    });
}

// 플랜 리스트 렌더링 함수
function renderPlanBoardList(boardList) {
    let boardContainer = $('#plan-board-list');
    boardContainer.empty();

    boardList.forEach(function(board) {
        let tags = [board.tag1, board.tag2, board.tag3, board.tag4, board.tag5, board.tag6];
        let tagHtml = '';

        tags.forEach(function(tag) {
            if (tag) {
                tagHtml += `<span>#${i18next.t(tag)}</span> `;
            }
        });

        let boardHtml = `
            <div class="col-6 col-sm-6 col-md-6 col-lg-3 mb-4 mb-lg-0">
                <div class="media-1">
                    <a href="#" class="d-block mb-3" data-board-num-plan="${board.boardNum}">
                        <img src="${board.coverImageUrl}" alt="Image" class="img-fluid">
                    </a>
                </div>
                <span class="d-flex align-items-center loc mb-2">
                <table>
                    <tr>
                        <th style = "width: fit-content;">
                        <a href="#" class="like" data-board-num="${board.boardNum}">
                        <img id="like" src="/images/plan/하트.png" style="width: 25px; height: 25px;">
                        </a>
                        <span id="like-${board.boardNum}">${board.likeCount}</span></th>
                        <th>
                            <a href="#" class="save" data-board-num="${board.boardNum}">
                                <img id="save" src="/images/plan/북마크.png" style="width: 30px; height: 30px;">
                            </a>
                        </th>
                    </tr>
                </table>
                </span>
                <div class="d-flex align-items-center">
                    <div>
                        <h3><a href="#">${board.planName}</a></h3>
                        <div class="price ml-auto" style="border-bottom: 100px">
                            ${tagHtml}
                        </div>
                    </div>
                </div>
            </div>
        `;
        boardContainer.append(boardHtml);
    });
}

// 페이지네이션 렌더링 함수
function renderPagination(response) {
    const pagination = $('.pagination');
    pagination.find('li.page-number').remove(); // 기존 페이지 번호 버튼 제거

    const totalPages = response.totalPages;
    const currentPage = response.number + 1; // Spring의 Page는 0부터 시작

    // 이전 페이지 버튼 비활성화 처리
    if (!response.first) {
        $('#prev-page').removeClass('disabled');
    } else {
        $('#prev-page').addClass('disabled');
    }

    // 다음 페이지 버튼 비활성화 처리
    if (!response.last) {
        $('#next-page').removeClass('disabled');
    } else {
        $('#next-page').addClass('disabled');
    }

    // 페이지 번호 버튼 생성 (linkSize=1을 고려하여 이전과 다음 1페이지씩만 표시)
    const startPage = Math.max(currentPage - 1, 1);
    const endPage = Math.min(currentPage + 1, totalPages);

    for (let i = startPage; i <= endPage; i++) {
        const activeClass = (i === currentPage) ? ' active' : '';
        const pageItem = `<li class="page-item page-number${activeClass}">
                                    <a class="page-link" href="#">${i}</a>
                                  </li>`;
        $('#next-page').before(pageItem);
    }


    // 플랜 클릭시 보여주기 위함
    $(document).on('click', '.d-block.mb-3', function() {
        const boardNumPlan = $(this).data('board-num-plan'); // 클릭된 요소의 boardNum 가져오기

        // AJAX 요청
        $.ajax({
            url: 'selectPlan', // 컨트롤러 메서드 경로
            type: 'POST',
            data: { boardNum: boardNumPlan }, // boardNum을 전송
            success: function (response) {
                // 성공 시 처리할 로직
            },
            error: function () {
                alert('플랜 정보를 불러오는 중 오류가 발생했습니다.');
            }
        });
    });
}
