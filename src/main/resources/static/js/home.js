// AI 추천 버튼 클릭 시 링크로 이동
const aiButton = document.querySelector(".ai_button");
aiButton.onclick = () => {
    window.location.href = "/gptView/question";
};

// 슬라이더 기능
document.addEventListener("DOMContentLoaded", () => {
    let currentIndex = 0; // 현재 슬라이드 인덱스
    const slides = document.querySelectorAll(".hero .slides img"); // 슬라이드 이미지 목록
    const totalSlides = slides.length; // 총 이미지 개수

    // 슬라이드를 전환하는 함수
    function showSlide(index) {
        slides.forEach((slide, i) => {
            slide.classList.toggle("active", i === index); // 해당 인덱스의 이미지 활성화
        });
    }

    // 6초마다 슬라이드를 변경하는 함수
    function startSlider() {
        setInterval(() => {
            currentIndex = (currentIndex + 1) % totalSlides; // 인덱스 순환
            showSlide(currentIndex);
        }, 6000); // 6초마다 슬라이드 전환
    }

    // 슬라이더 시작
    startSlider();
});

// jQuery - Typed.js 애니메이션
$(function () {
    var typed = new Typed(".typed-words", {
        strings: ["ソウルに触る"], // 출력할 텍스트
        typeSpeed: 80, // 타이핑 속도
        backSpeed: 80, // 되돌리는 속도
        backDelay: 4000, // 되돌리기 전 대기 시간
        startDelay: 1000, // 시작 전 대기 시간
        loop: true, // 반복 여부
        showCursor: true, // 커서 표시 여부
    });
});
