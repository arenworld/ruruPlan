document.addEventListener("DOMContentLoaded", function () {
    // 요소 선택자
    const modalButton = document.querySelector(".write-link");
    const modal = document.querySelector(".writeModal");
    const cancelButton = document.querySelector(".cancel-button");
    const footer = document.querySelector(".page-footer");
    const contentArea = document.querySelector(".content-area");
    const writeFormButton = document.querySelector(".writeForm-button");
    const count = document.querySelector(".count");
    const uploadButton = document.getElementById("uploadButton");
    const imageInput = document.getElementById("imageInput");
    const previewImage = document.getElementById("previewImage");
    const modal_overlay = document.querySelector(".modal-overlay");

    // 모달 열기
    modalButton.addEventListener("click", function (event) {
        event.preventDefault();
        //console.log('모달 열기 동작 실행됨');
        modal.style.display = "block";
        modal_overlay.style.display = "block";
        footer.style.visibility = "hidden";
        writeFormButton.style.backgroundColor = "gray";
        writeFormButton.disabled = true; // 비활성화 상태로 시작
        count.textContent = "0/1000자";
    });

    // 모달 닫기
    cancelButton.addEventListener("click", function () {
        modal.style.display = "none";
        modal_overlay.style.display = "none";
        footer.style.visibility = "visible";
        document.body.style.overflow = "auto";
        contentArea.value = ""; // 입력 필드의 내용을 초기화
        contentLength.value="0";
    });

    // 글자수 세기 및 버튼 활성화 로직
    contentArea.addEventListener("input", function () {
        const contentLength = contentArea.value.length;
        count.textContent = `${contentLength}/1000자`;

        if (contentLength >= 5 && contentLength <= 1000) {
            writeFormButton.style.backgroundColor = "#66b2e4";
            writeFormButton.disabled = false; // 버튼 활성화
        } else {
            writeFormButton.style.backgroundColor = "gray";
            writeFormButton.disabled = true; // 버튼 비활성화
        }
    });

    // 이미지 업로드 버튼 클릭 시 파일 선택 창 열기
    uploadButton.addEventListener("click", function () {
        imageInput.click();
    });

    // 파일 선택 시 미리보기 표시
    imageInput.addEventListener("change", function () {
        const file = imageInput.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                previewImage.src = e.target.result;
                previewImage.style.display = "block";
            };
            reader.readAsDataURL(file);
        }
    });


    // 스크롤 위치에 따라 헤더를 숨기거나 보이는 함수
    function hideHeaderWhenAtTop() {
        const header = document.querySelector(".page-header"); // 헤더 요소 선택
        if (window.scrollY === 0) {
            // 스크롤 위치가 0이면 페이지가 맨 위에 있다는 의미
            header.style.display = "none"; // 헤더를 숨김
        } else {
            header.style.display = "flex"; // 스크롤을 내렸을 때 헤더를 다시 표시
        }
    }
// 스크롤 이벤트 리스너 추가
    window.addEventListener('scroll', hideHeaderWhenAtTop);
});
