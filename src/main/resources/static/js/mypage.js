const profileEditButton = document.querySelector(".profile-edit-button");
const imageInput = document.getElementById("imageInput");
const profileImg = document.querySelector(".profile-img");

// 이미지 업로드 버튼 클릭 시 파일 선택 창 열기
profileEditButton.addEventListener("click", function () {
    imageInput.click();
});

// 파일 선택 후 이미지 미리보기 및 서버로 업로드
imageInput.addEventListener("change", function () {
    const file = imageInput.files[0];

    if (file) {
        // 미리보기
        const reader = new FileReader();
        reader.onload = function (e) {
            profileImg.src = e.target.result;
        };
        reader.readAsDataURL(file);

        // 서버로 파일 업로드
        const formData = new FormData();
        formData.append("profileImage", file);

        // CSRF 토큰을 메타 태그에서 가져오기
        const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

        // 서버로 파일 업로드
        fetch('/profile/uploadImage', {
            method: 'POST',
            headers: {
                [header]: token  // CSRF 헤더와 토큰을 추가
            },
            body: formData  // Content-Type은 생략해야 함
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert("프로필 이미지가 성공적으로 업데이트되었습니다.");
                } else {
                    alert("이미지 업로드에 실패했습니다.");
                }
            })
            .catch(error => {
                console.error("에러 발생:", error);
                alert("서버에 오류가 발생했습니다.");
            });

    }
});
