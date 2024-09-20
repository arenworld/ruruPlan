/*
*  질문 1    캘린더에서 선택시 이벤트를 담당하는 자바스크립트
* */

// DOMContentLoaded 이벤트: HTML 문서가 모두 로드된 후 실행될 코드
document.addEventListener("DOMContentLoaded", function () {

  // 결과를 표시할 박스와 '다음' 버튼 요소를 선택
  const resultBox = document.querySelector(".box-result-round");
  const nextButton = document.querySelector(".button-next");

  //필요한 텍스트
  let startDateT = $("#startDateT").text();
  let endDateT = $("#endDateT").text();
  let pleseSelectT = $("#pleaseSelectT").text();
  let nightT = $("#nightsT").text();
  let daysT = $("#daysT").text();
  let monthT = $("#monthT").text();
  let yearT = $("#yearT").text();

  // 초기 텍스트를 '출발일을 선택해 주세요'로 설정
  resultBox.textContent = startDateT + pleseSelectT;

  // 날짜 선택, 인덱스, 시작일 및 마지막 일자를 저장할 변수들 초기화
  let selectedDate = null;
  let clickedDateIndex = null;
  let startDay = null;
  let lastDay = null;

  // 초기에는 '다음' 버튼 숨기기
  nextButton.style.display = "none";

  // 달력 업데이트 후 이벤트 리스너 적용
  document.addEventListener('calendarsUpdated', function() {
    applySelectEventListeners();
  });

  // 달력 셀에 마우스 오버, 마우스 아웃, 클릭 이벤트 리스너 추가 함수
  function applySelectEventListeners() {
    const calendarCells = document.querySelectorAll(".calendar-date"); // 날짜 셀만 선택

    // 각 셀에 대해 이벤트 리스너를 추가
    calendarCells.forEach((cell, index) => {

      // 마우스 오버 이벤트
      cell.addEventListener("mouseover", function () {
        if (!cell.classList.contains("disabled") && !cell.classList.contains("dates-selected") && !cell.classList.contains("highlighted")) {
          this.style.backgroundColor = "#e0e0e0";
        }
      });

      // 마우스 아웃 이벤트
      cell.addEventListener("mouseout", function () {
        if (!cell.classList.contains("disabled") && !cell.classList.contains("dates-selected") && !cell.classList.contains("highlighted")) {
          this.style.backgroundColor = "#ffffff";
        }
      });

      // 셀 클릭 시 이벤트 처리
      cell.addEventListener("click", function () {
        // 동일한 셀을 다시 클릭했을 때 초기화
        if (startDay !== null && startDay === index) {
          resetCalendarStates(calendarCells);
          startDay = null;
          lastDay = null;
          displayMessage(startDateT + pleseSelectT);
          nextButton.style.display = "none";
          document.getElementById("startDayValue").value = "";
          document.getElementById("endDayValue").value = "";
          return;
        }

        // 출발일과 도착일이 모두 선택된 경우 초기화
        if (startDay !== null && lastDay !== null) {
          resetCalendarStates(calendarCells);
          startDay = null;
          lastDay = null;
          nextButton.style.display = "none";
          document.getElementById("startDayValue").value = "";
          document.getElementById("endDayValue").value = "";
        }

        // 출발일 선택
        if (startDay === null) {
          startDay = index;
          selectedDate = cell;
          document.getElementById("startDayValue").value = cell.textContent;
          clickedDateIndex = index;

          // 먼저 셀 상태를 업데이트
          updateCellStates(clickedDateIndex, calendarCells);

          // 그 후에 선택한 셀의 스타일을 변경
          selectedDate.classList.add("dates-selected");
          selectedDate.style.backgroundColor = "#000000";
          selectedDate.style.color = "#ffffff";

          displayMessage(endDateT  + pleseSelectT);
          nextButton.style.display = "none";
        } else if (startDay !== null && index >= startDay && index <= startDay + 5) {
          // 도착일 선택
          lastDay = index;
          document.getElementById("endDayValue").value = cell.textContent;

          const lastDateCell = calendarCells[lastDay];
          lastDateCell.classList.add("dates-selected");
          lastDateCell.style.backgroundColor = "#000000";
          lastDateCell.style.color = "#ffffff";

          highlightRange(startDay, lastDay, calendarCells);

          // 선택한 날짜 간격 계산
          const nights = lastDay - startDay;
          const days = nights + 1;
          document.getElementById("nightsValue").value = nights;
          document.getElementById("daysValue").value = days;
          console.log("박수:", nights, "일수:", days);

          // 선택된 날짜 정보 표시
          const startDate = calendarCells[startDay].textContent;
          const endDate = calendarCells[lastDay].textContent;
          const calendarHeader = cell.closest(".calendar").querySelector(".calendar-header").textContent;
          displayResult(nights, days, calendarHeader, startDate, endDate);
          nextButton.style.display = "block";  // '다음' 버튼 보이기
        }
      });
    });
  }

  // 셀 상태 업데이트: 클릭된 셀 이후 5일 이내의 셀만 활성화
  function updateCellStates(clickedDateIndex, calendarCells) {
    const totalCells = calendarCells.length;
    for (let i = 0; i < totalCells; i++) {
      const cell = calendarCells[i];
      if (i >= clickedDateIndex && i <= clickedDateIndex + 5) {
        // 선택 가능한 범위의 셀들
        cell.classList.remove("disabled");
        cell.style.backgroundColor = "#ffffff";
        if (cell.classList.contains("red")) {
          cell.style.color = "red";
        } else {
          cell.style.color = "#000000";
        }
      } else {
        // 선택 불가능한 범위의 셀들
        cell.classList.add("disabled");
        cell.style.backgroundColor = "#ffffff";
        cell.style.color = "#e1e2e6"; // 회색으로 표시
      }
    }
  }

  // 선택된 날짜들을 초기 상태로 되돌림
  function resetCalendarStates(calendarCells) {
    calendarCells.forEach((cell) => {
      cell.classList.remove("dates-selected", "highlighted", "disabled", "hovered");
      cell.style.backgroundColor = "#ffffff";
      if (cell.classList.contains("red")) {
        cell.style.color = "red";
      } else {
        cell.style.color = "#000000";
      }
    });
  }

  // 시작일과 도착일 사이의 범위를 강조
  function highlightRange(startDayIndex, endDayIndex, calendarCells) {
    calendarCells.forEach((cell, index) => {
      if (index > startDayIndex && index < endDayIndex) {
        cell.classList.add("highlighted");
        cell.style.backgroundColor = "#e0e0e0";
        cell.style.color = "#000000";
      }
    });
  }

  // 선택한 결과를 화면에 표시
  function displayResult(nights, days, calendarHeader, startDate, endDate) {
    const resultBox = document.querySelector(".box-result-round");
    const monthMatch = calendarHeader.match(/(\d{1,2})/);
    const monthText = monthMatch ? monthMatch[1].padStart(2, "0") : "";  // 월을 두 자리로 맞춤

    const first_date_display = `${window.currentYear}${yearT} ${monthText}${monthT} ${String(startDate).padStart(2, "0")}${daysT}`;
    const last_date_display = `${window.currentYear}${yearT} ${monthText}${monthT} ${String(endDate).padStart(2, "0")}${daysT}`;

    resultBox.textContent = `${nights}${nightT} ${days}${daysT}, ${startDateT}: ${first_date_display}, ${endDateT}: ${last_date_display}`;

    const first_date = `${window.currentYear}-${monthText}-${String(startDate).padStart(2, "0")}`;
    const last_date = `${window.currentYear}-${monthText}-${String(endDate).padStart(2, "0")}`;

    document.getElementById("first_date").value = first_date;
    document.getElementById("last_date").value = last_date;

    return { first_date, last_date };
  }

  // 메시지 표시
  function displayMessage(message) {
    const resultBox = document.querySelector(".box-result-round");
    resultBox.textContent = message;
  }

  // 모달이 열릴 때 days 값을 다시 가져오는 코드
  document.getElementById("myModal6").addEventListener("shown.bs.modal", function () {
    const days = document.getElementById("daysValue").value;
    console.log("모달에서 days 값:", days);

    if (!days) {
      console.log("days 값이 아직 설정되지 않았습니다.");
    }
  });
});
