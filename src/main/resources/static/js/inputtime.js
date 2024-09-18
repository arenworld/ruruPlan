document.addEventListener("DOMContentLoaded", function () {
  const reservation = document.querySelector(".button-reservation");
  const notreservation = document.querySelector(".button-notreservation");
  const nextButton2 = document.querySelector(".button-next-page2");
  const resultBox = document.querySelector(".box-reservation-result-round");
  let reservationChoice = null;

  // 초기에는 버튼 및 결과 박스 숨기기
  nextButton2.style.display = "none";
  resultBox.style.display = "none";

  // 예약 버튼 클릭 이벤트
  reservation.onclick = function () {
    // startDayValue, endDayValue, arrival, depart 요소가 존재하는지 확인
    const startDayValueElement = document.getElementById("startDayValue");
    const endDayValueElement = document.getElementById("endDayValue");
    const startTimeElement = document.getElementById("arrival");
    const endTimeElement = document.getElementById("depart");

    // 각 요소가 존재하는지 null 체크
    const startDayValue = startDayValueElement ? startDayValueElement.value : null;
    const endDayValue = endDayValueElement ? endDayValueElement.value : null;
    const startTimeValue = startTimeElement ? startTimeElement.value : null;
    const endTimeValue = endTimeElement ? endTimeElement.value : null;

    const calendarHeader = document.querySelector(".calendar-header").textContent;
    const monthMatch = calendarHeader.match(/(\d{1,2})월/);
    const monthText = monthMatch ? monthMatch[1] : ""; // '월' 값 추출

    // 값이 유효한지 확인
    if (startDayValue && endDayValue) {
      resultBox.innerHTML = `
        <div class="date-time-wrapper">
          <strong>입국일자: 2024년 ${monthText}월 ${startDayValue}일</strong>
          <input type="time" id="startTime" name="startTime" value="${startTimeValue || ""}" />
        </div>
        <div class="date-time-wrapper">
          <strong>출국일자: 2024년 ${monthText}월 ${endDayValue}일</strong>
          <input type="time" id="endTime" name="endTime" value="${endTimeValue || ""}" />
        </div>
      `;
      reservationChoice = 1; // 예약 선택 상태 설정

      resultBox.style.display = "block"; // 결과 박스 보이기
      reservation.style.backgroundColor = "gray"; // 클릭 시 배경색 변경
      notreservation.style.backgroundColor = ""; // 예약 안 함 버튼 초기화

      // 시간 입력 필드가 모두 채워졌는지 확인하는 함수
      function updateNextButtonState() {
        const startTimeValue = document.getElementById("startTime").value;
        const endTimeValue = document.getElementById("endTime").value;

        if (startTimeValue && endTimeValue) {
          nextButton2.style.display = "block"; // 시간이 입력된 경우 버튼 보이기
        } else {
          nextButton2.style.display = "none"; // 시간이 입력되지 않으면 버튼 숨기기
        }
      }

      // 시간 입력 필드에 이벤트 리스너 추가
      document.getElementById("startTime").addEventListener("input", updateNextButtonState);
      document.getElementById("endTime").addEventListener("input", updateNextButtonState);

      // 다음 버튼 표시 여부 결정
      updateNextButtonState();
    } else {
      resultBox.textContent = "날짜를 먼저 선택해 주세요.";
      resultBox.style.display = "block"; // 유효하지 않은 경우에도 메시지를 표시
    }
  };

  // 예약 안 함 버튼 클릭 이벤트
  notreservation.onclick = function () {
    // 이미 예약 안 함 버튼이 선택된 상태라면 아무 동작도 하지 않음
    if (reservationChoice === 0) {
      return;
    }

    reservationChoice = 0; // 예약 안 함 선택 상태 설정
    notreservation.style.backgroundColor = "gray"; // 클릭 시 배경색 변경
    reservation.style.backgroundColor = ""; // 예약 버튼 색상 초기화

    // 예약 안 함이 클릭되면 다음 버튼 숨기기 및 결과 박스 숨기기
    nextButton2.style.display = "block";
    resultBox.style.display = "none";
  };

  // 예약 상태를 초기화하는 함수
  function resetReservation() {
    reservationChoice = null; // 선택 상태 초기화
    resultBox.style.display = "none"; // 결과 박스 숨기기
    nextButton2.style.display = "none"; // next 버튼 숨기기

    // 버튼 색상 및 상태 초기화
    reservation.style.backgroundColor = "";
    notreservation.style.backgroundColor = "";
  }
});
