document.addEventListener("DOMContentLoaded", function () {
  const themes = document.querySelectorAll(
    ".button-theme-row1 button, .button-theme-row2 button, .button-theme-row3 button"
  );
  const themeBoxes = document.querySelectorAll(".theme-box .theme");
  const themeWeight1 = document.getElementById("theme_1_weight");
  const themeWeight2 = document.getElementById("theme_2_weight");
  const themeWeight3 = document.getElementById("theme_3_weight");
  const theme_1_box = document.querySelector(".theme_1-box");
  const theme_2_box = document.querySelector(".theme_2-box");
  const theme_3_box = document.querySelector(".theme_3-box");

  const cancelButtons = document.querySelectorAll(
    ".theme-cancel1, .theme-cancel2, .theme-cancel3"
  );
  const nextButton = document.querySelector(".button-next-page4");

  let selectedThemes = [];
  let selectedWeights = [];
  let closeTimeouts = [null, null, null]; // 각각의 가중치 창에 대한 타이머
  const weightBoxes = [themeWeight1, themeWeight2, themeWeight3]; // 가중치 박스 배열

  // 처음에 다음 버튼 숨기기
  nextButton.style.visibility = "hidden";
  theme_1_box.style.visibility = "hidden";
  theme_2_box.style.visibility = "hidden";
  theme_3_box.style.visibility = "hidden";

  // 테마 버튼 클릭 이벤트 처리
  themes.forEach((theme) => {
    theme.addEventListener("click", function () {
      if (selectedThemes.length >= 3) {
        alert("최대 3개의 테마만 선택 가능합니다.");
        return;
      }

      if (selectedThemes.some((t) => t.theme === theme.value)) {
        // 이미 선택된 테마는 다시 클릭되지 않도록 방지
        return;
      }

      theme.style.backgroundColor = "gray"; // 클릭한 테마 버튼을 회색으로 변경
      theme.disabled = true; // 이미 선택된 테마는 다시 클릭되지 않도록 설정

      themeBoxes[selectedThemes.length].textContent = theme.value; // 테마 박스에 선택한 테마 표시
      selectedThemes.push({ theme: theme.value, weight: null });

      // 테마 박스 가시화
      if (selectedThemes.length === 1) theme_1_box.style.visibility = "visible";
      if (selectedThemes.length === 2) theme_2_box.style.visibility = "visible";
      if (selectedThemes.length === 3) theme_3_box.style.visibility = "visible";

      showWeightSelector(selectedThemes.length - 1); // 가중치 선택창을 표시
    });
  });

  // 가중치 선택 창 마우스 오버 및 아웃 이벤트
  weightBoxes.forEach((weightBox, index) => {
    const themeBox = [theme_1_box, theme_2_box, theme_3_box][index]; // 각 테마 박스와 연결

    themeBox.addEventListener("mouseover", function () {
      weightBox.style.visibility = "visible"; // 마우스 오버 시 가중치 선택 창을 표시
      clearTimeout(closeTimeouts[index]); // 타이머를 초기화
    });

    themeBox.addEventListener("mouseleave", function () {
      // 10초 후 가중치 선택 창 숨기기
      closeTimeouts[index] = setTimeout(function () {
        if (!selectedWeights[index]) {
          weightBox.style.visibility = "hidden"; // 선택된 가중치가 없으면 창 숨김
        }
      }, 10000); // 10초 후 창 닫기
    });

    // 가중치 값 선택 시 이벤트
    weightBox.querySelectorAll("div").forEach((weightOption) => {
      weightOption.addEventListener("click", function () {
        selectedWeights[index] = weightOption.id; // 가중치 값을 저장
        weightBox.style.visibility = "hidden"; // 선택 후 가중치 창 닫기
        checkSelectionComplete(); // 선택이 완료되면 확인 후 다음 버튼 표시
      });
    });
  });

  // 가중치 선택 창을 표시하는 함수
  function showWeightSelector(index) {
    weightBoxes[index].style.visibility = "visible"; // 가중치 창을 표시
    clearTimeout(closeTimeouts[index]); // 타이머 초기화
  }

  // 모든 테마와 가중치 선택이 완료되었는지 확인하는 함수
  function checkSelectionComplete() {
    if (
      selectedThemes.length === 3 &&
      selectedWeights.every((weight) => weight !== null)
    ) {
      nextButton.style.visibility = "visible"; // 모든 선택이 완료되면 다음 버튼 표시
    } else {
      nextButton.style.visibility = "hidden"; // 선택이 완료되지 않으면 다음 버튼 숨기기
    }
  }

  // 테마 취소 버튼 클릭 이벤트
  cancelButtons.forEach((cancelButton, index) => {
    cancelButton.addEventListener("click", function () {
      if (selectedThemes[index]) {
        // 선택 취소 시 처리 로직
        const removedTheme = selectedThemes[index].theme;

        // 배열에서 해당 인덱스 삭제
        selectedThemes.splice(index, 1);
        selectedWeights.splice(index, 1);

        // 취소한 인덱스 이후로 배열 업데이트
        for (let i = index; i < 3; i++) {
          if (i < selectedThemes.length) {
            themeBoxes[i].textContent = selectedThemes[i].theme; // 다음 테마로 갱신
          } else {
            themeBoxes[i].textContent = ""; // 비우기
            weightBoxes[i].style.visibility = "hidden"; // 가중치 창 숨기기
            const themeBox = [theme_1_box, theme_2_box, theme_3_box][i];
            themeBox.style.visibility = "hidden"; // 테마 박스 숨기기
          }
        }

        // 취소한 테마의 버튼 복구
        themes.forEach((theme) => {
          if (theme.value === removedTheme) {
            theme.style.backgroundColor = ""; // 선택 취소 시 버튼 색상 복구
            theme.disabled = false; // 다시 클릭할 수 있도록 활성화
          }
        });

        checkSelectionComplete(); // 선택 완료 여부 다시 확인
      }
    });
  });

  // 선택된 테마와 가중치에 마우스가 들어가면 가중치를 수정할 수 있도록 처리
  [theme_1_box, theme_2_box, theme_3_box].forEach((box, index) => {
    box.addEventListener("mouseover", function () {
      if (selectedWeights[index]) {
        weightBoxes[index].style.visibility = "visible"; // 선택된 테마에 마우스 오버 시 가중치 수정 가능
        weightBoxes[index].querySelectorAll("div").forEach((weightOption) => {
          weightOption.style.backgroundColor =
            selectedWeights[index] === weightOption.id ? "darkgray" : ""; // 선택된 가중치는 진한 회색으로 표시
        });
      }
      clearTimeout(closeTimeouts[index]); // 타이머 초기화
    });

    box.addEventListener("mouseleave", function () {
      // 10초 후 가중치 선택 창 숨기기
      closeTimeouts[index] = setTimeout(function () {
        if (selectedWeights[index]) {
          weightBoxes[index].style.visibility = "hidden"; // 가중치 창 숨기기
        }
      }, 200); // 10초 후 창 닫기
    });
  });
});
