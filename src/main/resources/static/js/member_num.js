/**
 * 질문3 에대한 자바 스크립트
 *
 * 문제점
 *
 * 1.인원수 0명 입력도 가능하다 아이랑 어른 입력때문에 일단은 허용은해놓음 
 *
 *
 * 사용법
 *
 * 혼자 , 커플은 자동적으로 각각 어른 1 아이0 어른2 아이영으로 값저장
 * 
 *친구 , 부모님은 어른 칸만 나오며 숫자인지 범위내에 들어오는지 검증

 아이는 합이 6이하이며 검증내용은 위의 내용과 같음
 *
 *
 */
document.addEventListener("DOMContentLoaded", function () {
  const alone = document.getElementById("button-alone");
  const couple = document.getElementById("button-couple");
  const kids = document.getElementById("button-kids");
  const parents = document.getElementById("button-parents");
  const friend = document.getElementById("button-friend");
  let children_num = document.getElementById("children");
  let adult_num = document.getElementById("adult");
  const next_button3 = document.querySelector(".button-next-page3");
  const num_resultBox = document.querySelector(".box-who-result-round");
  const adultInputDiv = num_resultBox.querySelector(".adult");
  const kidsInputDiv = num_resultBox.querySelector(".kids");

  console.log("adult_num element:", adult_num); // adult_num이 제대로 선택되었는지 확인

  // 상태를 추적하는 변수 (true: 활성화 상태, false: 비활성화 상태)
  let aloneActive = false;
  let coupleActive = false;
  let kidsActive = false;
  let parentsActive = false;
  let friendActive = false;

  // 처음에는 다음버튼과 인원수 버튼 숨김
  next_button3.style.display = "none";
  num_resultBox.style.display = "none";

  // 다른 버튼 상태 초기화 함수
  function resetButtons() {
    alone.style.backgroundColor = "";
    couple.style.backgroundColor = "";
    kids.style.backgroundColor = "";
    parents.style.backgroundColor = "";
    friend.style.backgroundColor = "";

    // 각 버튼의 활성화 상태를 초기화
    aloneActive = false;
    coupleActive = false;
    kidsActive = false;
    parentsActive = false;
    friendActive = false;

    // 값 초기화
    adult_num.value = "";
    children_num.value = "";

    // 결과 및 버튼 초기화
    next_button3.style.display = "none";
    num_resultBox.style.display = "none";
  }

  // 혼자 버튼 클릭 시
  alone.onclick = function () {
    resetButtons(); // 다른 버튼 상태 초기화
    if (!aloneActive) {
      couple.style.backgroundColor = "gray";
      kids.style.backgroundColor = "gray";
      parents.style.backgroundColor = "gray";
      friend.style.backgroundColor = "gray";
      next_button3.style.display = "block";
      adult_num.value = 1; // 혼자이므로 어른 수 1명
      children_num.value = 0; // 혼자이므로 아이 수는 0명
      console.log(adult_num.value); // 어른 수를 콘솔에 출력
    }
    aloneActive = !aloneActive; // 상태를 토글
  };

  // 커플 버튼 클릭 시
  couple.onclick = function () {
    resetButtons(); // 다른 버튼 상태 초기화
    if (!coupleActive) {
      alone.style.backgroundColor = "gray";
      kids.style.backgroundColor = "gray";
      parents.style.backgroundColor = "gray";
      friend.style.backgroundColor = "gray";
      next_button3.style.display = "block";
      adult_num.value = 2; // 커플이므로 어른 수 2명
      children_num.value = 0; // 커플이므로 아이 수는 0명
    }
    coupleActive = !coupleActive; // 상태를 토글
  };

  // 아이 버튼 클릭 시
  kids.onclick = function () {
    resetButtons(); // 다른 버튼 상태 초기화
    if (!kidsActive) {
      couple.style.backgroundColor = "gray";
      alone.style.backgroundColor = "gray";
      parents.style.backgroundColor = "gray";
      friend.style.backgroundColor = "gray";
      num_resultBox.style.display = "block";
      adultInputDiv.style.display = "block";
      kidsInputDiv.style.display = "block";
    }
    kidsActive = !kidsActive; // 상태를 토글
  };

  // 부모님 버튼 클릭 시 추가
  parents.onclick = function () {
    resetButtons(); // 다른 버튼 상태 초기화
    if (!parentsActive) {
      alone.style.backgroundColor = "gray";
      couple.style.backgroundColor = "gray";
      kids.style.backgroundColor = "gray";
      friend.style.backgroundColor = "gray";
      num_resultBox.style.display = "block";
      adultInputDiv.style.display = "block";
      kidsInputDiv.style.display = "none";
    }
    parentsActive = !parentsActive; // 상태를 토글
  };

  // 친구 버튼 클릭 시 추가
  friend.onclick = function () {
    resetButtons(); // 다른 버튼 상태 초기화
    if (!friendActive) {
      alone.style.backgroundColor = "gray";
      couple.style.backgroundColor = "gray";
      kids.style.backgroundColor = "gray";
      parents.style.backgroundColor = "gray";

      // 어른 입력칸만 보이기
      num_resultBox.style.display = "block";
      adultInputDiv.style.display = "block";
      kidsInputDiv.style.display = "none";
      console.log(adult_num.value);

      friendActive = true;

      // adult_num가 제대로 선택되는지 다시 한 번 확인
      if (adult_num) {
        console.log("Adding input event listener to adult_num");

        // 어른 수 입력 값 검증 함수 (input 이벤트 처리)
        adult_num.addEventListener("input", function () {
          console.log("Input event triggered!");
          validateTotalInput();
        });

        // 아이 수 입력 값 검증 함수 (input 이벤트 처리)
        children_num.addEventListener("input", function () {
          console.log("Input event triggered!");
          validateTotalInput();
        });
      }
    } else {
      friendActive = false; // 토글 상태 전환
    }
  };

  // 어른과 아이 수 입력 값 검증 함수
  function validateTotalInput() {
    const adultValue = parseInt(adult_num.value) || 0; // 어른 값, 없으면 0
    const childrenValue = parseInt(children_num.value) || 0; // 아이 값, 없으면 0

    console.log(
      "adult_num value:",
      adultValue,
      "children_num value:",
      childrenValue
    ); // 로그로 값 확인

    // 숫자가 아닌 값이 입력된 경우
    if (isNaN(adultValue) || isNaN(childrenValue)) {
      alert("숫자를 입력해 주세요");
      children_num.value = ""; // 잘못된 값일 경우 아이 수 초기화
      adult_num.value = ""; // 잘못된 값일 경우 어른 수 초기화
      next_button3.style.display = "none"; // 다음 버튼 숨기기
      return; // 검증 함수 종료
    }

    // 어른 수가 1~6 범위를 벗어난 경우
    if (adultValue < 0 || adultValue > 6) {
      alert("인원 수는 최소 1명, 최대 6명이어야 합니다.");
      adult_num.value = ""; // 잘못된 값일 경우 어른 수 초기화
      next_button3.style.display = "none"; // 다음 버튼 숨기기
      return; // 검증 함수 종료
    }

    // 어른과 아이 수의 합이 6명을 넘는지 확인
    if (adultValue + childrenValue > 6) {
      alert("어른과 아이 수의 합은 최대 6명이어야 합니다.");
      children_num.value = ""; // 잘못된 값일 경우 아이 수 초기화
      next_button3.style.display = "none"; // 다음 버튼 숨기기
    } else {
      next_button3.style.display = "block"; // 올바른 값일 경우 다음 버튼 표시
    }
  }
});
