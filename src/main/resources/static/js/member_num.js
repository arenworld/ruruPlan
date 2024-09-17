document.addEventListener("DOMContentLoaded", function () {
  const alone = document.getElementById("button-alone");
  const couple = document.getElementById("button-couple");
  const kids = document.getElementById("button-kids");
  const parents = document.getElementById("button-parents");
  const friend = document.getElementById("button-friend");

  const next_button3 = document.querySelector(".button-next-page3");
  const num_resultBox = document.querySelector(".box-who-result-round");
  const adultInputDiv = num_resultBox.querySelector(".adult");
  const kidsInputDiv = num_resultBox.querySelector(".kids");
  const trip_type = document.getElementById("trip_type");

  // 상태를 추적하는 변수 (true: 활성화 상태, false: 비활성화 상태)
  let aloneActive = false;
  let coupleActive = false;
  let kidsActive = false;
  let parentsActive = false;
  let friendActive = false;

  // 처음에는 다음버튼과 인원수 입력칸 숨김
  next_button3.style.visibility = "hidden";
  num_resultBox.style.visibility = "hidden";

  // 다른 버튼 상태 초기화 함수
  function resetButtons() {
    alone.style.backgroundColor = "";
    couple.style.backgroundColor = "";
    kids.style.backgroundColor = "";
    parents.style.backgroundColor = "";
    friend.style.backgroundColor = "";
    trip_type.textContent = ""; // 여행 유형 초기화
    // 각 버튼의 활성화 상태를 초기화
    aloneActive = false;
    coupleActive = false;
    kidsActive = false;
    parentsActive = false;
    friendActive = false;

    // 값 초기화
    if (adult_num) adult_num.value = "";
    if (children_num) children_num.value = "";

    // 결과 및 버튼 초기화
    next_button3.style.visibility = "hidden";
    num_resultBox.style.visibility = "hidden";
    trip_type.textContent = ""; // 여행 유형 초기화
  }

  // 혼자 버튼 클릭 시
  alone.onclick = function () {
    resetButtons(); // 다른 버튼 상태 초기화
    if (!aloneActive) {
      trip_type.textContent = "혼자";
      alone.style.backgroundColor = "gray";
      couple.style.backgroundColor = "white";
      kids.style.backgroundColor = "white";
      parents.style.backgroundColor = "white";
      friend.style.backgroundColor = "white";
      next_button3.style.visibility = "visible";
      num_resultBox.style.visibility = "hidden";
      adultInputDiv.style.visibility = "hidden";
      kidsInputDiv.style.visibility = "hidden";

      // 어른 수와 아이 수 설정
      const adult_num = document.getElementById("adult");
      const children_num = document.getElementById("children");
      if (adult_num) adult_num.value = 1; // 혼자이므로 어른 수 1명
      if (children_num) children_num.value = 0; // 혼자이므로 아이 수는 0명
    }
    aloneActive = !aloneActive; // 상태를 토글
  };

  // 커플 버튼 클릭 시
  couple.onclick = function () {
    resetButtons(); // 다른 버튼 상태 초기화
    if (!coupleActive) {
      trip_type.textContent = "커플";
      couple.style.backgroundColor = "gray";
      alone.style.backgroundColor = "white";
      kids.style.backgroundColor = "white";
      parents.style.backgroundColor = "white";
      friend.style.backgroundColor = "white";
      next_button3.style.visibility = "visible";
      num_resultBox.style.visibility = "hidden";
      adultInputDiv.style.visibility = "hidden";
      kidsInputDiv.style.visibility = "hidden";

      // 어른 수와 아이 수 설정
      const adult_num = document.getElementById("adult");
      const children_num = document.getElementById("children");
      if (adult_num) adult_num.value = 2; // 커플이므로 어른 수 2명
      if (children_num) children_num.value = 0; // 커플이므로 아이 수는 0명
    }
    coupleActive = !coupleActive; // 상태를 토글
  };

  // 아이 버튼 클릭 시
  kids.onclick = function () {
    resetButtons(); // 다른 버튼 상태 초기화
    if (!kidsActive) {
      trip_type.textContent = "아이";
      kids.style.backgroundColor = "gray";
      couple.style.backgroundColor = "white";
      alone.style.backgroundColor = "white";
      parents.style.backgroundColor = "white";
      friend.style.backgroundColor = "white";
      num_resultBox.style.visibility = "visible";
      adultInputDiv.style.visibility = "visible";
      kidsInputDiv.style.visibility = "visible";

      next_button3.style.visibility = "hidden"; // 다음 버튼 숨김

      // 어른 수와 아이 수 초기화
      const adult_num = document.getElementById("adult");
      const children_num = document.getElementById("children");
      if (adult_num) adult_num.value = "";
      if (children_num) children_num.value = "";
    }
    kidsActive = !kidsActive; // 상태를 토글
  };

  // 부모님 버튼 클릭 시
  parents.onclick = function () {
    resetButtons(); // 다른 버튼 상태 초기화
    if (!parentsActive) {
      trip_type.textContent = "부모님";
      parents.style.backgroundColor = "gray";
      alone.style.backgroundColor = "white";
      couple.style.backgroundColor = "white";
      kids.style.backgroundColor = "white";
      friend.style.backgroundColor = "white";
      num_resultBox.style.visibility = "visible";
      adultInputDiv.style.visibility = "visible";
      kidsInputDiv.style.visibility = "hidden";

      next_button3.style.visibility = "hidden"; // 다음 버튼 숨김

      // 어른 수와 아이 수 초기화
      const adult_num = document.getElementById("adult");
      const children_num = document.getElementById("children");
      if (adult_num) adult_num.value = "";
      if (children_num) children_num.value = 0; // 아이 수는 0명
    }
    parentsActive = !parentsActive; // 상태를 토글
  };

  // 친구 버튼 클릭 시
  friend.onclick = function () {
    resetButtons(); // 다른 버튼 상태 초기화
    if (!friendActive) {
      trip_type.textContent = "친구";
      friend.style.backgroundColor = "gray";
      alone.style.backgroundColor = "white";
      couple.style.backgroundColor = "white";
      kids.style.backgroundColor = "white";
      parents.style.backgroundColor = "white";
      num_resultBox.style.visibility = "visible";
      adultInputDiv.style.visibility = "visible";
      kidsInputDiv.style.visibility = "hidden";

      next_button3.style.visibility = "hidden"; // 다음 버튼 숨김

      // 어른 수와 아이 수 초기화
      const adult_num = document.getElementById("adult");
      const children_num = document.getElementById("children");
      if (adult_num) adult_num.value = "";
      if (children_num) children_num.value = 0; // 아이 수는 0명
    }
    friendActive = !friendActive; // 상태를 토글
  };

  // 어른 수 입력 값 검증 함수
  function validateTotalInput() {
    const adult_num = document.getElementById("adult");
    const children_num = document.getElementById("children");
    const adultValue = parseInt(adult_num.value) || 0; // 어른 값, 없으면 0
    const childrenValue = parseInt(children_num.value) || 0; // 아이 값, 없으면 0

    // 숫자가 아닌 값이 입력된 경우
    if (isNaN(adultValue) || isNaN(childrenValue)) {
      alert("숫자를 입력해 주세요");
      if (children_num) children_num.value = ""; // 잘못된 값일 경우 아이 수 초기화
      if (adult_num) adult_num.value = ""; // 잘못된 값일 경우 어른 수 초기화
      next_button3.style.visibility = "hidden"; // 다음 버튼 숨기기
      return; // 검증 함수 종료
    }

    // 어른 수가 1~6 범위를 벗어난 경우
    if (adultValue < 0 || adultValue > 6) {
      alert("인원 수는 최소 1명, 최대 6명이어야 합니다.");
      if (adult_num) adult_num.value = ""; // 잘못된 값일 경우 어른 수 초기화
      next_button3.style.visibility = "hidden"; // 다음 버튼 숨기기
      return; // 검증 함수 종료
    }

    // 어른과 아이 수의 합이 6명을 넘는지 확인
    if (adultValue + childrenValue > 6) {
      alert("어른과 아이 수의 합은 최대 6명이어야 합니다.");
      if (children_num) children_num.value = ""; // 잘못된 값일 경우 아이 수 초기화
      next_button3.style.visibility = "hidden"; // 다음 버튼 숨기기
    } else {
      next_button3.style.visibility = "visible"; // 올바른 값일 경우 다음 버튼 표시
    }
  }

  // 이벤트 리스너를 전역적으로 등록
  const adult_num = document.getElementById("adult");
  const children_num = document.getElementById("children");

  if (adult_num) {
    adult_num.addEventListener("input", validateTotalInput);
  }

  if (children_num) {
    children_num.addEventListener("input", validateTotalInput);
  }
});
