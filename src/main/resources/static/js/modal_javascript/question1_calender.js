/*
*  질문 1    날짜선택을 위한 캘린더를 만들고 공휴일을 표사를 담당하는 자바 스크립트
* */

document.addEventListener("DOMContentLoaded", async () => {
  const apiKey = "F4yv4m0bUBkXqSmWQ"; // Calendarific API 키
  const countryCodes = ["JP", "KR"]; // 일본(JP)과 한국(KR)의 국가 코드

  const today = new Date();
  let currentMonth = today.getMonth();
  window.currentYear = today.getFullYear(); // 전역 변수로 설정하여 다른 파일에서 접근 가능

  // 달력을 업데이트하는 함수
  async function updateCalendars() {
    const calendar1 = document.getElementById("calendar-1");
    const calendar2 = document.getElementById("calendar-2");

    const firstMonth = new Date(window.currentYear, currentMonth, 1);
    const secondMonth = new Date(window.currentYear, currentMonth + 1, 1);

    // 달력 업데이트
    await fetchCalendarData(firstMonth, calendar1, countryCodes);
    await fetchCalendarData(secondMonth, calendar2, countryCodes);

    // 날짜 선택 이벤트 리스너를 적용하기 위해 custom 이벤트를 발생시킵니다.
    document.dispatchEvent(new Event('calendarsUpdated'));
  }

  // 공휴일 데이터를 가져와서 달력을 업데이트하는 함수
  async function fetchCalendarData(date, calendar, countryCodes) {
    const year = date.getFullYear(); // date 객체에서 연도 추출
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
      dayNameDiv.classList.add("calendar-day-name");
      dayNameDiv.textContent = day;
      grid.appendChild(dayNameDiv);
    });

    // 날짜 셀 생성
    const daysInMonth = new Date(year, month, 0).getDate();
    const firstDay = new Date(year, month - 1, 1).getDay();

    for (let i = 0; i < firstDay; i++) {
      const emptyCell = document.createElement("div");
      emptyCell.classList.add("empty");
      grid.appendChild(emptyCell);
    }

    const dayCells = [];
    for (let day = 1; day <= daysInMonth; day++) {
      const dayDiv = document.createElement("div");
      dayDiv.textContent = day;
      dayDiv.classList.add("calendar-cell", "calendar-date"); // 날짜 셀에 'calendar-date' 클래스 추가

      const dateStr = `${year}-${String(month).padStart(2, "0")}-${String(day).padStart(2, "0")}`;
      const dateObj = new Date(dateStr);

      // 주말이면 빨간색으로 표시
      if (dateObj.getDay() === 0 || dateObj.getDay() === 6) {
        dayDiv.classList.add("red");
      }

      dayCells.push({ dateStr, dayDiv });
      grid.appendChild(dayDiv);
    }

    // 각 국가의 공휴일 표시
    const holidayPromises = countryCodes.map(async (countryCode) => {
      const url = `https://calendarific.com/api/v2/holidays?api_key=${apiKey}&country=${countryCode}&year=${year}`;

      try {
        const response = await fetch(url);
        const data = await response.json();
        const holidays = data.response.holidays;

        holidays.forEach((holiday) => {
          const holidayDate = holiday.date.iso;

          dayCells.forEach((dayCell) => {
            if (dayCell.dateStr === holidayDate) {
              if (countryCode === "JP") {
                dayCell.dayDiv.classList.add("holiday");
              } else if (countryCode === "KR") {
                dayCell.dayDiv.classList.add("korean-holiday");
              }
            }
          });
        });
      } catch (error) {
        console.error("공휴일 데이터를 가져오는 중 오류 발생:", error);
      }
    });

    await Promise.all(holidayPromises);

    calendar.appendChild(grid);
  }

  // 이전 달, 다음 달 버튼 클릭 핸들러
  document.getElementById("prev-month").addEventListener("click", () => {
    currentMonth -= 1;
    if (currentMonth < 0) {
      currentMonth += 12;
      window.currentYear--;
    }
    updateCalendars();
  });

  document.getElementById("next-month").addEventListener("click", () => {
    currentMonth += 1;
    if (currentMonth > 11) {
      currentMonth -= 12;
      window.currentYear++;
    }
    updateCalendars();
  });

  // 페이지 로드 시 현재 달력 표시
  updateCalendars();
});
