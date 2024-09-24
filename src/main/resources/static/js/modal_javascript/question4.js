let selectedThemeValues = []; // 전역 변수로 선언하여 두 스크립트에서 공유

document.addEventListener("DOMContentLoaded", function () {
  const themes = document.querySelectorAll(
      ".button-theme-row1 button, .button-theme-row2 button, .button-theme-row3 button:not(#leisuresports), .button-theme-row4 button"
  );

  const themeBoxes = document.querySelectorAll(".theme-box .theme");
  const theme_1_box = document.querySelector(".theme_1-box");
  const theme_2_box = document.querySelector(".theme_2-box");
  const theme_3_box = document.querySelector(".theme_3-box");

  const leisure_sports = document.getElementById("leisuresports");
  const bike = document.getElementById("bike");
  const water_skiing = document.getElementById("water_skiing");
  const ice_rink = document.getElementById("ice_rink");
  const sportsbox = [leisure_sports, bike, water_skiing, ice_rink];

  const ment1 = document.querySelector(".coment-ratio");
  const ment2 = document.querySelector(".coment-ratio2");

  const nextButton = document.querySelector(".button-next-page4");

  let selectedThemes = []; // 화면에 표시될 국제화된 텍스트를 저장할 배열

  // 테마와 해당 이미지 경로 매핑 일본어
  const themeImages = {
    쇼핑: "/images/modal/쇼핑_수정.png",
    ショッピング:"/images/modal/쇼핑_수정.png",
    음식: "/images/modal/음식이모티콘.png",
    食べ物: "/images/modal/음식이모티콘.png", // 일본어와 한국어 모두 동일한 경로 사용
    카페: "/images/modal/카페이모티콘.png",
    カフェ: "/images/modal/카페이모티콘.png",
    역사: "/images/modal/역사_수정.png",
    歴史:"/images/modal/역사_수정.png",
    문화: "/images/modal/문화이모티콘.png",
    文化:"/images/modal/문화이모티콘.png",
    힐링: "/images/modal/힐링이모티콘.png",
    ヒーリング:"/images/modal/힐링이모티콘.png",
    체험: "/images/modal/체험이모티콘.png",
    体験:"/images/modal/체험이모티콘.png",
    랜드마크: "/images/modal/랜드마크이모티콘.png",
    ランドマーク:"/images/modal/랜드마크이모티콘.png",
    레포츠: "/images/modal/레포츠이모티콘.png",
    レポーツ:"/images/modal/레포츠이모티콘.png",
    수상레저: "/images/modal/수상이모티콘.png",
    水上レジャー:"/images/modal/수상이모티콘.png",
    자전거: "/images/modal/자전거이모티콘.png",
    自転車:"/images/modal/자전거이모티콘.png",
    아이스링크: "/images/modal/아이스링크이모티콘.png",
    アイスリンク: "/images/modal/아이스링크이모티콘.png"
  };

  // 처음에 '다음' 버튼과 테마 박스 숨기기
  nextButton.style.visibility = "hidden";
  theme_1_box.style.visibility = "hidden";
  theme_2_box.style.visibility = "hidden";
  theme_3_box.style.visibility = "hidden";
  bike.style.display = "none";
  water_skiing.style.display = "none";
  ice_rink.style.display = "none";
  ment2.style.display = "none";

  // 테마 버튼 클릭 이벤트 처리
  themes.forEach((theme) => {
    theme.addEventListener("click", function () {
      const saveText = theme.getAttribute("value"); // 버튼의 value 속성으로 DB에 보낼 값
      const themeText = theme.querySelector("span").textContent; // 화면에 표시할 <span> 텍스트

      // 이미 선택된 테마를 다시 클릭했을 경우 (취소)
      if (selectedThemes.some((t) => t === themeText)) {
        const themeIndex = selectedThemes.indexOf(themeText);
        selectedThemes.splice(themeIndex, 1); // 화면에 표시될 목록에서 제거
        selectedThemeValues.splice(themeIndex, 1); // DB로 보낼 value 목록에서 제거
        theme.style.backgroundColor = ""; // 배경색 원래대로
        updateThemeBoxes(); // 테마 박스 재정렬
        checkSelectionComplete(); // 선택 완료 여부 확인
        checkSelectionSports(); // 레포츠 상태 확인
        return;
      }

      let themeAlert1 = $("#themeAlert1").text();
      // 선택된 테마가 3개 이상일 경우 추가 선택을 방지
      if (selectedThemes.length >= 3) {
        alert(themeAlert1);
        return;
      }

      // 새로운 테마 선택
      theme.style.backgroundColor = "gray"; // 클릭한 테마 버튼을 회색으로 변경
      selectedThemes.push(themeText); // 화면에 표시될 목록에 추가
      selectedThemeValues.push(saveText); // DB로 보낼 목록에 추가
      updateThemeBoxes(); // 테마 박스 재정렬
      checkSelectionComplete(); // 선택 완료 여부 확인
      checkSelectionSports(); // 레포츠 상태 확인
    });
  });

  // 선택된 테마를 테마 박스에 재정렬하여 표시하고 이미지와 텍스트를 저장하는 함수
  function updateThemeBoxes() {
    themeBoxes.forEach((box) => {
      box.innerHTML = ""; // 기존 내용 제거
      box.style.visibility = "hidden";
    });

    // 선택된 테마에 맞게 박스에 이미지와 텍스트 표시
    selectedThemes.forEach((themeText, index) => {
      const themeValue = selectedThemeValues[index]; // DB로 보낼 값
      const themeImg = document.createElement("img");
      themeImg.src = themeImages[themeValue]; // 이미지 경로 설정
      themeImg.alt = themeText; // 대체 텍스트 설정
      themeImg.style.width = "50px"; // 이미지 크기 조정

      const themeDisplayText = document.createElement("span");
      themeDisplayText.textContent = themeText; // 화면에 표시할 텍스트 설정

      themeBoxes[index].appendChild(themeImg); // 이미지 추가
      themeBoxes[index].appendChild(themeDisplayText); // 텍스트 추가
      themeBoxes[index].style.visibility = "visible"; // 보이도록 설정
    });
  }

  // 레포츠 관련 상태 확인 함수
  function checkSelectionSports() {
    const sportsSelected = selectedThemeValues.some((value) =>
        ["자전거", "아이스링크", "수상레저"].includes(value)
    );

    // 스포츠 테마가 하나라도 선택되었을 경우 레포츠 버튼 회색으로 변경
    if (sportsSelected) {
      leisure_sports.style.backgroundColor = "gray";
    } else {
      leisure_sports.style.backgroundColor = "white";
    }
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

      bike.style.display = "block";
      ice_rink.style.display = "block";
      water_skiing.style.display = "block";

      ment1.style.display = "none"; // 기존 멘트 숨기기
      ment2.style.display = "block"; // 새로운 멘트 보이기

      theme_1_box.style.display = "none";
      theme_2_box.style.display = "none";
      theme_3_box.style.display = "none";
    });

    element.addEventListener("mouseleave", function () {
      hoverTimeout = setTimeout(function () {
        theme_1_box.style.display = "block";
        theme_2_box.style.display = "block";
        theme_3_box.style.display = "block";

        ment1.style.display = "block"; // 기존 멘트 다시 보이기
        ment2.style.display = "none"; // 새로운 멘트 숨기기

        bike.style.display = "none";
        water_skiing.style.display = "none";
        ice_rink.style.display = "none";
      }, 500); // 1초 후에 원래 상태로 복귀
    });
  });
});
