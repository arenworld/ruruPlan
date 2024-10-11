/*
 *  질문3 여행타입(친구,커플,부모님,아이,혼자)를 파악하고
 *  어른과 아이 수를 설정하는 자바스크립트
 */

document.addEventListener("DOMContentLoaded", function () {
  const alone = document.getElementById("button-alone");
  const couple = document.getElementById("button-couple");
  const kids = document.getElementById("button-kids");
  const parents = document.getElementById("button-parents");
  const friend = document.getElementById("button-friend");
  const next_button3 = document.querySelector(".button-next-page3");
  const trip_type = document.getElementById("trip_type");

  // 어른과 아이 수를 저장할 숨겨진 input 요소
  const adult_num = document.getElementById("adult");
  const children_num = document.getElementById("children");

  // 상태를 추적하는 변수 (true: 활성화 상태, false: 비활성화 상태)
  let aloneActive = false;
  let coupleActive = false;
  let kidsActive = false;
  let parentsActive = false;
  let friendActive = false;

  // 처음에는 다음버튼을 숨김
  next_button3.style.visibility = "hidden";

  // 다른 버튼 상태 초기화 함수
  function resetButtons() {
    // 모든 버튼의 스타일 초기화
    alone.style.border = "1px solid skyblue";
    couple.style.border = "1px solid skyblue";
    kids.style.border = "1px solid skyblue";
    parents.style.border = "1px solid skyblue";
    friend.style.border = "1px solid skyblue";
    trip_type.value = ""; // 여행 유형 초기화

    // 각 버튼의 활성화 상태를 초기화
    aloneActive = false;
    coupleActive = false;
    kidsActive = false;
    parentsActive = false;
    friendActive = false;

    // 어른과 아이 수 초기화
    if (adult_num) adult_num.value = "";
    if (children_num) children_num.value = "";

    // 다음 버튼 숨기기
    next_button3.style.visibility = "hidden";
  }

  // 혼자 버튼 클릭 시
  alone.onclick = function () {
    resetButtons(); // 다른 버튼 상태 초기화
    if (!aloneActive) {
      trip_type.value = "혼자"; // 여행 유형 설정
      //console.log(trip_type.value);
      alone.style.setProperty("border", "5px solid skyblue", "important");
      next_button3.style.visibility = "visible"; // 다음 버튼 표시

      // 어른 수와 아이 수 설정
      if (adult_num) adult_num.value = 1; // 혼자이므로 어른 수 1명
      if (children_num) children_num.value = 0; // 아이 수는 0명
    }
    aloneActive = !aloneActive; // 상태를 토글
  };

  // 커플 버튼 클릭 시
  couple.onclick = function () {
    resetButtons(); // 다른 버튼 상태 초기화
    if (!coupleActive) {
      trip_type.value = "커플"; // 여행 유형 설정
      //console.log(trip_type.value);
      couple.style.setProperty("border", "5px solid skyblue", "important");
      next_button3.style.visibility = "visible"; // 다음 버튼 표시

      // 어른 수와 아이 수 설정
      if (adult_num) adult_num.value = 2; // 커플이므로 어른 수 2명
      if (children_num) children_num.value = 0; // 아이 수는 0명
    }
    coupleActive = !coupleActive; // 상태를 토글
  };

  // 아이 버튼 클릭 시
  kids.onclick = function () {
    resetButtons(); // 다른 버튼 상태 초기화
    if (!kidsActive) {
      trip_type.value = "아이"; // 여행 유형 설정
      //console.log(trip_type.value);
      kids.style.setProperty("border", "5px solid skyblue", "important");
      next_button3.style.visibility = "visible"; // 다음 버튼 표시

      // 어른 수와 아이 수 설정 (예시로 어른 2명, 아이 2명 설정)
      if (adult_num) adult_num.value = 2;
      if (children_num) children_num.value = 2;
    }
    kidsActive = !kidsActive; // 상태를 토글
  };

  // 부모님 버튼 클릭 시
  parents.onclick = function () {
    resetButtons(); // 다른 버튼 상태 초기화
    if (!parentsActive) {
      trip_type.value = "부모님"; // 여행 유형 설정
      //console.log(trip_type.value);
      parents.style.setProperty("border", "5px solid skyblue", "important");
      next_button3.style.visibility = "visible"; // 다음 버튼 표시

      // 어른 수와 아이 수 설정 (예시로 어른 2명 설정)
      if (adult_num) adult_num.value = 2;
      if (children_num) children_num.value = 0; // 아이 수는 0명
    }
    parentsActive = !parentsActive; // 상태를 토글
  };

  // 친구 버튼 클릭 시
  friend.onclick = function () {
    resetButtons(); // 다른 버튼 상태 초기화
    if (!friendActive) {
      trip_type.value = "친구"; // 여행 유형 설정
     //console.log(trip_type.value);
      friend.style.setProperty("border", "5px solid skyblue", "important");
      next_button3.style.visibility = "visible"; // 다음 버튼 표시

      // 어른 수와 아이 수 설정 (예시로 어른 3명 설정)
      if (adult_num) adult_num.value = 3;
      if (children_num) children_num.value = 0; // 아이 수는 0명
    }
    friendActive = !friendActive; // 상태를 토글
  };
});
