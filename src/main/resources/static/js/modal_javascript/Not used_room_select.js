/*   질문 1에서 선택한 일자를 바탕으로  숙소를 선택하게하는 자바스크립트

      하지만 쓰이는일은 없었다
*
* */

document.addEventListener("DOMContentLoaded", function () {
  const submit_button = document.querySelector(".modal-submit-button");
  const move_button = document.getElementById("move");
  const no_move_button = document.getElementById("no-move");
  const harmonious = document.getElementById("harmonious");
  const moderately = document.getElementById("moderately");
  const relaxed = document.getElementById("relaxed");
  const mixed = document.getElementById("mixed");
  const row2 = document.querySelector(".button-move_status-row2");
  const row3 = document.querySelector(".button-move_status-row3");
  const move_status = document.getElementById("move_status");
  const room_cost = document.getElementById("room_cost");

  // 날짜 값을 표시할 요소
  const remainingDaysDisplay = document.getElementById("remainingDaysDisplay");

  // initialDaysValue 초기화
  let initialDaysValue = 0;
  let remainingDays = 0;

  // 날짜 값을 화면에 반영하는 함수
  function updateRemainingDaysDisplay() {
    remainingDaysDisplay.textContent = remainingDays;
  }

  // 모든 input-container를 가져오기
  const input_containers = document.querySelectorAll(".input-container");

  // 일수
  const hotel_nights = document.getElementById("hotel_nights");
  const motel_nights = document.getElementById("motel_nights");
  const guesthouse_nights = document.getElementById("guesthouse_nights");
  const hanok_nights = document.getElementById("hanok_nights");
  const coment_night = document.querySelector(".coment-night");

  // 숙소 버튼
  const buttons = {
    hotel: document.querySelector(".hotel-button"),
    motel: document.querySelector(".motel-button"),
    guesthouse: document.querySelector(".guesthouse-button"),
    hanok: document.querySelector(".hanok-button"),
  };

  // 종류
  const hotel = document.getElementById("hotel");
  const motel = document.getElementById("motel");
  const guesthouse = document.getElementById("guesthouse");
  const hanok = document.getElementById("hanok");

  // 초기화 함수 - 모달이 열리면 초기화
  function initializeDaysValue() {
    const daysValueField = document.getElementById("daysValue");
    initialDaysValue = parseInt(daysValueField.value) || 0; // 한 번만 설정
    remainingDays = initialDaysValue; // remainingDays만 변경 가능
    //console.log("초기화 - initialDaysValue:", initialDaysValue);
    updateRemainingDaysDisplay();
  }

  // 처음 화면에서 다음 버튼 및 2, 3행 숨김
  row2.style.visibility = "hidden";
  row3.style.visibility = "hidden";
  submit_button.style.visibility = "hidden";
  // input_containers 배열의 각 요소를 숨김 처리
  input_containers.forEach(function (container) {
    container.style.visibility = "hidden";
  });

  // 이동O 버튼을 누르면
  move_button.onclick = function () {
    initializeDaysValue(); // 초기화
    room_cost_reset();
    room_style_reset();
    day_reset();
    move_status.value = 1; // 이동O 상태
    no_move_button.style.backgroundColor = "#087ed7";
    move_button.style.backgroundColor = "gray"; // 이동버튼 gray로
    row2.style.visibility = "visible"; // 2열 보이기
    row3.style.visibility = "hidden"; // 3열 숨기기

    submit_button.style.visibility = "hidden"; // 제출버튼 숨기기
    remainingDays = initialDaysValue; // 남은 일수 초기화
    updateRemainingDaysDisplay(); // 화면에 반영
    hideAllNightsInput();
    //console.log("이동O 버튼 클릭 후 remainingDays 값:", remainingDays);
  };

  // 이동X 버튼을 누르면
  no_move_button.onclick = function () {
    initializeDaysValue(); // 초기화
    room_cost_reset();
    room_style_reset();
    day_reset();
    move_status.value = 0; // 이동X 상태
    no_move_button.style.backgroundColor = "gray";
    move_button.style.backgroundColor = "#087ed7";
    row2.style.visibility = "visible"; // 2열 보이기
    row3.style.visibility = "hidden"; // 3열 숨기기
    // input_containers 배열의 각 요소를 숨김 처리
    input_containers.forEach(function (container) {
      container.style.visibility = "hidden";
    });

    submit_button.style.visibility = "hidden"; // 제출버튼 숨기기
    remainingDays = initialDaysValue; // 남은 일수 초기화
    hideAllNightsInput();
    updateRemainingDaysDisplay(); // 화면에 반영
    //console.log("이동X 버튼 클릭 후 remainingDays 값:", remainingDays);
  };

  // 호화롭게
  harmonious.onclick = function () {
    room_style_reset();
    room_cost_reset();
    room_cost.value = 1;
    harmonious.style.backgroundColor = "gray";
    handleRoomOptions();
  };
  // 적당하게
  moderately.onclick = function () {
    room_style_reset();
    room_cost_reset();
    room_cost.value = 2;
    moderately.style.backgroundColor = "gray";
    handleRoomOptions();
  };

  // 저렴하게
  relaxed.onclick = function () {
    room_style_reset();
    room_cost_reset();
    room_cost.value = 3;
    relaxed.style.backgroundColor = "gray";
    handleRoomOptions();
  };

  // 섞어서
  mixed.onclick = function () {
    room_style_reset();
    room_cost_reset();
    room_cost.value = 4;
    mixed.style.backgroundColor = "gray";
    handleRoomOptions();
  };

  // 방 옵션 선택 시 동작하는 함수
  function handleRoomOptions() {
    row3.style.visibility = "visible"; // row3는 항상 보이게 설정

    if (move_status.value == "1") {
      showAllNightsInput();
    } else if (move_status.value == "0") {
      hideAllNightsInput();
    }
  }

  // 모든 박 입력란을 숨기는 함수
  function hideAllNightsInput() {
    input_containers.forEach(function (container) {
      container.style.display = "none"; // 박 입력란을 숨김
    });
    coment_night.innerHTML = "숙소 타입을 알려주세요"; // 숙소 선택 메시지
  }

  function showAllNightsInput() {
    let totalNights =
      (parseInt(hotel_nights.value) || 0) +
      (parseInt(motel_nights.value) || 0) +
      (parseInt(guesthouse_nights.value) || 0) +
      (parseInt(hanok_nights.value) || 0);

    // 남은 일수를 계산
    remainingDays = initialDaysValue - totalNights;

    coment_night.innerHTML = `숙소타입과 몇 밤 묵을지 알려주세요 (남은 일수는 ${remainingDays}일 입니다)`;
    updateRemainingDaysDisplay(); // 화면에 남은 일수 반영

    input_containers.forEach(function (container) {
      container.style.visibility = "visible";
    });
  }

  // 가격 스타일 리셋 함수
  function room_cost_reset() {
    room_cost.value = "";
    harmonious.style.backgroundColor = "#087ed7";
    moderately.style.backgroundColor = "#087ed7";
    relaxed.style.backgroundColor = "#087ed7";
    mixed.style.backgroundColor = "#087ed7";
  }

  // 숙소 스타일 리셋함수
  function room_style_reset() {
    hotel.value = "";
    motel.value = "";
    guesthouse.value = "";
    hanok.value = "";
    buttons.hotel.style.backgroundColor = "#087ed7";
    buttons.motel.style.backgroundColor = "#087ed7";
    buttons.guesthouse.style.backgroundColor = "#087ed7";
    buttons.hanok.style.backgroundColor = "#087ed7";
  }

  // 일자 입력 리셋 함수
  function day_reset() {
    // 모든 박수 입력란의 값을 초기화

    hotel_nights.value = "";
    motel_nights.value = "";
    guesthouse_nights.value = "";
    hanok_nights.value = "";

    // remainingDays를 initialDaysValue로 다시 설정
    remainingDays = initialDaysValue;

    // daysValue 값을 다시 초기값으로 설정
    document.getElementById("daysValue").value = initialDaysValue;

    // 화면에 남은 일수를 업데이트
    updateRemainingDaysDisplay();

    // 콘솔에 초기화된 값을 확인하기 위한 출력
    //console.log("일자 리셋 - remainingDays:", remainingDays);
    //console.log("일자 리셋 - initialDaysValue:", initialDaysValue);
  }

  // 숙소 버튼 클릭 공통 함수
  function handleRoomButtonClick(type) {
    if (move_status.value == "0") {
      room_style_reset();
      buttons[type].style.backgroundColor = "gray";
      document.getElementById(type).value = "1";
      submit_button.style.visibility = "visible";
    }
  }

  // 박수 입력 처리 함수
  function handleNightInput() {
    let totalNights =
      (parseInt(hotel_nights.value) || 0) +
      (parseInt(motel_nights.value) || 0) +
      (parseInt(guesthouse_nights.value) || 0) +
      (parseInt(hanok_nights.value) || 0);

    // remainingDays만 변경하고 initialDaysValue는 그대로 유지
    if (totalNights > initialDaysValue) {
      alert(`총 숙박일수가 남은 일수 (${remainingDays})를 초과할 수 없습니다.`);
      this.value = ""; // 현재 입력 중인 숙소 박수 입력란의 값을 초기화
    } else {
      remainingDays = initialDaysValue - totalNights; // remainingDays 갱신
      updateRemainingDaysDisplay(); // 남은 일수를 화면에 반영
      //console.log("남은 일수 (remainingDays):", remainingDays);
      coment_night.innerHTML = `숙소타입과 몇 밤 묵을지 알려주세요 (남은 일수는 ${remainingDays}일 입니다)`;

      // 남은 일수가 0일 때만 submit 버튼을 보이도록 함
      if (remainingDays === 0) {
        submit_button.style.visibility = "visible";
      } else {
        submit_button.style.visibility = "hidden";
      }
    }
  }
  // 박수 입력란에 이벤트 리스너 추가
  hotel_nights.addEventListener("input", handleNightInput);
  motel_nights.addEventListener("input", handleNightInput);
  guesthouse_nights.addEventListener("input", handleNightInput);
  hanok_nights.addEventListener("input", handleNightInput);
  // 숙소 버튼 클릭 이벤트 바인딩
  buttons.hotel.onclick = function () {
    handleRoomButtonClick("hotel");
  };
  buttons.motel.onclick = function () {
    handleRoomButtonClick("motel");
  };
  buttons.guesthouse.onclick = function () {
    handleRoomButtonClick("guesthouse");
  };
  buttons.hanok.onclick = function () {
    handleRoomButtonClick("hanok");
  };
});
