document.addEventListener("DOMContentLoaded", function () {
  // '레포츠' 버튼을 제외한 테마 버튼 선택
  const themes = document.querySelectorAll(
      ".button-theme-row1 button:not(#leisuresports), .button-theme-row2 button:not(#leisuresports), .button-theme-row3 button:not(#leisuresports)"
  );

  const themeBoxes = document.querySelectorAll(".theme-box .theme");
  const theme_1_box = document.querySelector(".theme_1-box");
  const theme_2_box = document.querySelector(".theme_2-box");
  const theme_3_box = document.querySelector(".theme_3-box");

  const nextButton = document.querySelector(".button-next-page4");

  const leisure_sports = document.getElementById("leisuresports");
  const bike = document.getElementById("bike");
  const water_skiing = document.getElementById("water_skiing");
  const ice_rink = document.getElementById("ice_rink");
  const cafe = document.getElementById("cafe");
  const healing = document.getElementById("healing");

  const sportsbox = [leisure_sports, bike, water_skiing, ice_rink];

  let selectedThemes = [];
  let hoverTimeout;

  // 테마와 해당 이미지 경로 매핑
  const themeImages = {
    쇼핑: "/images/쇼핑이모티콘.png",
    음식: "/images/음식이모티콘.png",
    카페: "/images/카페이모티콘.png",
    역사: "/images/역사이모티콘.png",
    문화: "/images/문화이모티콘.png",
    힐링: "/images/힐링이모티콘.png",
    체험: "/images/체험이모티콘.png",
    랜드마크: "/images/랜드마크이모티콘.png",
    레포츠: "/images/레포츠이모티콘.png",
    수상레저: "/images/수상이모티콘.png",
    자전거: "/images/자전거이모티콘.png",
    아이스링크: "/images/아이스링크이모티콘.png",
  };

  // 처음에 '다음' 버튼과 테마 박스 숨기기
  nextButton.style.visibility = "hidden";
  theme_1_box.style.visibility = "hidden";
  theme_2_box.style.visibility = "hidden";
  theme_3_box.style.visibility = "hidden";
  bike.style.display = "none";
  water_skiing.style.display = "none";
  ice_rink.style.display = "none";

  // 테마 버튼 클릭 이벤트 처리
  themes.forEach((theme) => {
    theme.addEventListener("click", function () {
      const themeValue = theme.value;

      // 이미 선택된 테마를 다시 클릭했을 경우 (취소)
      if (selectedThemes.some((t) => t === themeValue)) {
        const themeIndex = selectedThemes.indexOf(themeValue);
        selectedThemes.splice(themeIndex, 1); // 선택 목록에서 제거
        theme.style.backgroundColor = ""; // 배경색 원래대로
        updateThemeBoxes(); // 테마 박스 재정렬
        checkSelectionComplete(); // 선택 완료 여부 확인
        return;
      }

      // 선택된 테마가 3개 이상일 경우 추가 선택을 방지
      if (selectedThemes.length >= 3) {
        alert("최대 3개의 테마만 선택 가능합니다.");
        return;
      }

      // 새로운 테마 선택
      theme.style.backgroundColor = "gray"; // 클릭한 테마 버튼을 회색으로 변경
      selectedThemes.push(themeValue); // 선택 목록에 추가
      updateThemeBoxes(); // 테마 박스 재정렬
      checkSelectionComplete(); // 선택 완료 여부 확인
    });
  });

  // 선택된 테마를 테마 박스에 재정렬하여 표시하고 이미지와 텍스트를 저장하는 함수
  function updateThemeBoxes() {
    // 테마 박스 초기화
    themeBoxes.forEach((box) => {
      box.innerHTML = ""; // 기존 내용 제거
      box.style.visibility = "hidden";
    });

    // 선택된 테마에 맞게 박스에 이미지와 텍스트 표시
    selectedThemes.forEach((theme, index) => {
      const themeImg = document.createElement("img");
      themeImg.src = themeImages[theme]; // 이미지 경로 설정
      themeImg.alt = theme; // 대체 텍스트 설정
      themeImg.style.width = "50px"; // 이미지 크기 조정

      const themeText = document.createElement("span");
      themeText.textContent = theme; // 텍스트 설정

      themeBoxes[index].appendChild(themeImg); // 이미지 추가
      themeBoxes[index].appendChild(themeText); // 텍스트 추가
      themeBoxes[index].style.visibility = "visible"; // 보이도록 설정
    });
  }

  // 선택 완료 여부 확인하는 함수
  function checkSelectionComplete() {
    if (selectedThemes.length === 3) {
      nextButton.style.visibility = "visible"; // 3개 선택되면 '다음' 버튼 표시
    } else {
      nextButton.style.visibility = "hidden"; // 3개 미만이면 '다음' 버튼 숨기기
    }
  }

  // 레포츠 클릭 비활성화 (호버는 가능, 클릭 이벤트 무시)
  leisure_sports.addEventListener("click", function (event) {
    event.preventDefault(); // 클릭 이벤트 무시
  });

  // '레포츠' 그룹 마우스 오버 및 아웃 이벤트 처리
  sportsbox.forEach((element) => {
    element.addEventListener("mouseover", function () {
      clearTimeout(hoverTimeout); // 타이머 초기화
      healing.style.display = "none";
      cafe.style.display = "none";
      bike.style.display = "block";

      // 달력에서 월 정보를 가져옴
      const calendarHeader = document.querySelector(".calendar-header").textContent;
      const monthMatch = calendarHeader.match(/(\d{1,2})월/);
      const monthText = monthMatch ? parseInt(monthMatch[1]) : null;

      // 4월 ~ 11월: 수상스키 보이기, 12월 ~ 3월: 아이스링크 보이기
      if (monthText >= 4 && monthText <= 11) {
        water_skiing.style.display = "block";
        ice_rink.style.display = "none";
      } else if (monthText !== null) {
        ice_rink.style.display = "block";
        water_skiing.style.display = "none";
      }
    });

    element.addEventListener("mouseleave", function () {
      hoverTimeout = setTimeout(function () {
        healing.style.display = "block";
        cafe.style.display = "block";
        bike.style.display = "none";
        water_skiing.style.display = "none";
        ice_rink.style.display = "none";
      }, 500); // 1초 후에 원래 상태로 복귀
    });
  });
});
