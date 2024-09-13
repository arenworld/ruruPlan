document.addEventListener("DOMContentLoaded", function () {
  const themes = document.querySelectorAll(
      ".button-theme-row1 button, .button-theme-row2 button, .button-theme-row3 button"
  );
  const themeBoxes = document.querySelectorAll(".theme-box .theme");
  const theme_1_box = document.querySelector(".theme_1-box");
  const theme_2_box = document.querySelector(".theme_2-box");
  const theme_3_box = document.querySelector(".theme_3-box");

  const nextButton = document.querySelector(".button-next-page4");

  let selectedThemes = [];

  // 처음에 '다음' 버튼 숨기기
  nextButton.style.visibility = "hidden";
  theme_1_box.style.visibility = "hidden";
  theme_2_box.style.visibility = "hidden";
  theme_3_box.style.visibility = "hidden";

  // 테마 버튼 클릭 이벤트 처리
  themes.forEach((theme) => {
    theme.addEventListener("click", function () {
      const themeValue = theme.value;

      // 이미 선택된 테마를 다시 클릭했을 경우 (취소)
      if (selectedThemes.some((t) => t === themeValue)) {
        const themeIndex = selectedThemes.indexOf(themeValue);
        selectedThemes.splice(themeIndex, 1); // 선택 목록에서 제거
        theme.style.backgroundColor = ""; // 배경색 원래대로
        theme.disabled = false; // 다시 선택할 수 있도록 활성화
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

  // 선택된 테마를 테마 박스에 재정렬하여 표시하고 value 속성에 저장하는 함수
  function updateThemeBoxes() {
    // 테마 박스 초기화
    themeBoxes.forEach((box) => {
      box.textContent = "";
      box.style.visibility = "hidden";
      box.removeAttribute("value"); // 기존 value 속성 제거
    });

    // 선택된 테마에 맞게 박스에 재정렬하여 표시 및 value 설정
    selectedThemes.forEach((theme, index) => {
      themeBoxes[index].textContent = theme;
      themeBoxes[index].style.visibility = "visible";
      themeBoxes[index].setAttribute("value", theme); // 클릭한 값의 value를 설정
    });
  }

  // 선택 완료 여부 확인하는 함수
  function checkSelectionComplete() {
    if (selectedThemes.length === 3) {
      nextButton.style.visibility = "visible"; // 3개 선택되면 '다음' 버튼 표시
      console.log(
          theme_1_box.textContent,
          theme_2_box.textContent,
          theme_3_box.textContent
      );
    } else {
      nextButton.style.visibility = "hidden"; // 3개 미만이면 '다음' 버튼 숨기기
    }
  }
});
