document.addEventListener("DOMContentLoaded", () => {
  const apiKey = "EmibabV4ELAgWlj9zaKOmMmurWlOvXgG"; // Calendarific API 키
  const countryCodes = ["JP", "KR"]; // 일본(JP)과 한국(KR)의 국가 코드
  const year = "2024"; // 공휴일 데이터를 가져올 연도

  const today = new Date();
  let currentMonth = today.getMonth();
  let currentYear = today.getFullYear();

  // 달력을 업데이트하는 함수
  function updateCalendars() {
    const calendar1 = document.getElementById("calendar-1");
    const calendar2 = document.getElementById("calendar-2");

    const firstMonth = new Date(year, currentMonth, 1);
    const secondMonth = new Date(year, currentMonth + 1, 1);

    // 달력 업데이트 및 이벤트 리스너 적용
    fetchCalendarData(firstMonth, calendar1, countryCodes);
    fetchCalendarData(secondMonth, calendar2, countryCodes);
    applySelectEventListeners();
  }

  // 공휴일 데이터를 가져와서 달력을 업데이트하는 함수
  function fetchCalendarData(date, calendar, countryCodes) {
    const month = date.getMonth() + 1;
    const dayNames = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
    calendar.innerHTML = "";

    const header = document.createElement("div");
    header.classList.add("calendar-header");
    header.textContent = `${month}월 ${year}`;

    calendar.appendChild(header);

    const grid = document.createElement("div");
    grid.classList.add("calendar-grid");

    dayNames.forEach((day) => {
      const dayNameDiv = document.createElement("div");
      dayNameDiv.classList.add("calendar-cell", "day-name");
      dayNameDiv.textContent = day;
      grid.appendChild(dayNameDiv);
    });

    calendar.appendChild(grid);

    const daysInMonth = new Date(year, month, 0).getDate();
    const firstDay = new Date(year, month - 1, 1).getDay();

    for (let i = 0; i < firstDay; i++) {
      const emptyCell = document.createElement("div");
      emptyCell.classList.add("calendar-cell", "empty");
      grid.appendChild(emptyCell);
    }

    const dayCells = [];
    for (let day = 1; day <= daysInMonth; day++) {
      const dayDiv = document.createElement("div");
      dayDiv.textContent = day;
      dayDiv.classList.add("calendar-cell");

      const dateStr = `${year}-${String(month).padStart(2, "0")}-${String(
        day
      ).padStart(2, "0")}`;
      const dateObj = new Date(dateStr);

      // 주말이면 빨간색으로 표시
      if (dateObj.getDay() === 0 || dateObj.getDay() === 6) {
        dayDiv.classList.add("red");
      }

      dayCells.push({ dateStr, dayDiv });
      grid.appendChild(dayDiv);
    }

    // 각 국가의 공휴일 표시
    countryCodes.forEach((countryCode) => {
      const url = `https://calendarific.com/api/v2/holidays?api_key=${apiKey}&country=${countryCode}&year=${year}`;

      fetch(url)
        .then((response) => response.json())
        .then((data) => {
          const holidays = data.response.holidays;

          holidays.forEach((holiday) => {
            const holidayDate = holiday.date.iso;

            dayCells.forEach((dayCell) => {
              if (dayCell.dateStr === holidayDate) {
                if (countryCode === "JP") {
                  if (dayCell.dayDiv.classList.contains("korean-holiday")) {
                    dayCell.dayDiv.classList.add("purple-holiday");
                  } else {
                    dayCell.dayDiv.classList.add("holiday");
                  }
                } else if (countryCode === "KR") {
                  if (dayCell.dayDiv.classList.contains("holiday")) {
                    dayCell.dayDiv.classList.add("purple-holiday");
                  } else {
                    dayCell.dayDiv.classList.add("korean-holiday");
                  }
                }
              }
            });
          });
        })
        .catch((error) => {
          console.error("공휴일 데이터를 가져오는 중 오류 발생:", error);
        });
    });
  }

  // 선택된 날짜 및 스타일을 초기화하는 함수 (주말 및 공휴일 스타일 유지)
  function resetCalendarStates(calendarCells) {
    calendarCells.forEach((cell) => {
      if (
        !cell.classList.contains("red") &&
        !cell.classList.contains("holiday") &&
        !cell.classList.contains("korean-holiday") &&
        !cell.classList.contains("purple-holiday")
      ) {
        // 주말 및 공휴일이 아닌 날짜만 리셋
        cell.classList.remove("dates-selected");
        cell.style.backgroundColor = "#ffffff"; // 기본 배경색
        cell.style.color = "#000000"; // 기본 글자색
        cell.classList.remove("disabled");
      }
    });
  }

  // 이전 달, 다음 달 버튼 클릭 핸들러
  document.getElementById("prev-month").addEventListener("click", () => {
    currentMonth -= 1;
    if (currentMonth < 0) {
      currentMonth += 12;
      currentYear--;
    }
    updateCalendars();
  });

  document.getElementById("next-month").addEventListener("click", () => {
    currentMonth += 1;
    if (currentMonth > 11) {
      currentMonth -= 12;
      currentYear++;
    }
    updateCalendars();
  });

  // 페이지 로드 시 현재 달력 표시
  updateCalendars();
});
