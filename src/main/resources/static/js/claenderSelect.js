document.addEventListener("DOMContentLoaded", function () {
  const resultBox = document.querySelector(".box-result-round");
  const nextButton = document.querySelector(".button-next");
  resultBox.textContent = "출발일을 선택해 주세요";

  let selectedDate = null;
  let clickedDateIndex = null;
  let startDay = null;
  let lastDay = null;

  // 초기에는 버튼 숨기기
  nextButton.style.display = "none";

  // 달력 셀에 마우스 오버/아웃 및 클릭 이벤트 추가
  function applySelectEventListeners() {
    const calendarCells = document.querySelectorAll(".calendar-cell");

    calendarCells.forEach((cell, index) => {
      cell.addEventListener("mouseover", function () {
        if (
            !cell.classList.contains("disabled") &&
            !cell.classList.contains("dates-selected")
        ) {
          this.style.backgroundColor = "#e0e0e0";
        }
      });

      cell.addEventListener("mouseout", function () {
        if (
            !cell.classList.contains("disabled") &&
            !cell.classList.contains("dates-selected")
        ) {
          this.style.backgroundColor = "#ffffff";
        }
      });

      // 셀 클릭 이벤트
      cell.addEventListener("click", function () {
        if (startDay !== null && startDay === index) {
          resetCalendarStates(calendarCells);
          startDay = null;
          lastDay = null;
          displayMessage("출발일을 선택해 주세요");
          nextButton.style.display = "none";

          document.getElementById("startDayValue").value = "";
          document.getElementById("endDayValue").value = "";
          return;
        }

        if (startDay !== null && lastDay !== null) {
          resetCalendarStates(calendarCells);
          startDay = null;
          lastDay = null;
          nextButton.style.display = "none";

          document.getElementById("startDayValue").value = "";
          document.getElementById("endDayValue").value = "";
        }

        if (startDay === null) {
          startDay = index;
          selectedDate = cell;

          document.getElementById("startDayValue").value = cell.textContent;

          selectedDate.classList.add("dates-selected");
          selectedDate.style.backgroundColor = "#000000";
          selectedDate.style.color = "#ffffff";

          clickedDateIndex = index;

          updateCellStates(clickedDateIndex, calendarCells);
          displayMessage("도착일을 선택해 주세요");
          nextButton.style.display = "none";
        } else if (
            startDay !== null &&
            index >= startDay &&
            index <= startDay + 5
        ) {
          lastDay = index;
          document.getElementById("endDayValue").value = cell.textContent;

          const lastDateCell = calendarCells[lastDay];
          lastDateCell.classList.add("dates-selected");
          lastDateCell.style.backgroundColor = "#000000";
          lastDateCell.style.color = "#ffffff";

          highlightRange(startDay, lastDay, calendarCells);

          const nights = lastDay - startDay;
          const days = nights + 1;

          document.getElementById("nightsValue").value = nights;
          document.getElementById("daysValue").value = days;
          console.log("박수:", nights, "일수:", days);
          const startDate = calendarCells[startDay].textContent;
          const endDate = calendarCells[lastDay].textContent;
          const calendarHeader = cell
              .closest(".calendar")
              .querySelector(".calendar-header").textContent;

          displayResult(nights, days, calendarHeader, startDate, endDate);
          nextButton.style.display = "block";
        }
      });
    });
  }

  function updateCellStates(clickedDateIndex, calendarCells) {
    const totalCells = calendarCells.length;

    for (
        let i = clickedDateIndex;
        i < totalCells && i <= clickedDateIndex + 5;
        i++
    ) {
      calendarCells[i].classList.remove("disabled");
      calendarCells[i].style.backgroundColor = "#ffffff";
      calendarCells[i].style.color = "#000000";
    }

    for (let i = 0; i < totalCells; i++) {
      if (i < clickedDateIndex || i > clickedDateIndex + 5) {
        calendarCells[i].classList.add("disabled");
        calendarCells[i].style.backgroundColor = "#ffffff";
        calendarCells[i].style.color = "#e1e2e6";
      }
    }
  }

  function resetCalendarStates(calendarCells) {
    calendarCells.forEach((cell) => {
      if (cell.classList.contains("red")) {
        cell.style.backgroundColor = "white";
        cell.style.color = "red";
      } else {
        cell.classList.remove("dates-selected");
        cell.style.backgroundColor = "#ffffff";
        cell.style.color = "#000000";
        cell.classList.remove("disabled");
      }
    });
  }

  function highlightRange(startDayIndex, endDayIndex, calendarCells) {
    calendarCells.forEach((cell, index) => {
      if (index > startDayIndex && index < endDayIndex) {
        cell.style.backgroundColor = "#e0e0e0";
      }
    });
  }

  function displayResult(nights, days, calendarHeader, startDate, endDate) {
    const resultBox = document.querySelector(".box-result-round");
    const monthMatch = calendarHeader.match(/(\d{1,2})월/);
    const monthText = monthMatch ? monthMatch[1].padStart(2, "0") : ""; // 월을 두 자리로 맞춤

    // 디스플레이용 날짜 (포맷된 날짜)
    const first_date_display = `2024년 ${monthText}월 ${String(
        startDate
    ).padStart(2, "0")}일`;
    const last_date_display = `2024년 ${monthText}월 ${String(endDate).padStart(
        2,
        "0"
    )}일`;

    // 화면에 표시
    resultBox.textContent = `${nights}박 ${days}일, 시작일: ${first_date_display}, 종료일: ${last_date_display}`;

    // YYYY-MM-DD 형식으로 변환
    const first_date = `2024-${monthText}-${String(startDate).padStart(
        2,
        "0"
    )}`;
    const last_date = `2024-${monthText}-${String(endDate).padStart(2, "0")}`;

    // 숨겨진 필드에 값 저장
    document.getElementById("first_date").value = first_date;
    document.getElementById("last_date").value = last_date;

    // 반환값이 필요하면 return 가능
    return { first_date, last_date };
  }

  function displayMessage(message) {
    const resultBox = document.querySelector(".box-result-round");
    resultBox.textContent = message;
  }

  function updateCalendars() {
    applySelectEventListeners();
  }

  updateCalendars();

  document
      .getElementById("prev-month")
      .addEventListener("click", updateCalendars);
  document
      .getElementById("next-month")
      .addEventListener("click", updateCalendars);
});
// 모달이 열릴 때 days 값을 다시 가져오는 코드 추가
document
    .getElementById("myModal6")
    .addEventListener("shown.bs.modal", function () {
      const days = document.getElementById("daysValue").value;
      console.log("모달에서 days 값:", days); // 확인용 콘솔 출력

      // days 값이 없는 경우 처리
      if (!days) {
        console.log("days 값이 아직 설정되지 않았습니다.");
      }
    });
