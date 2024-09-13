document.addEventListener("DOMContentLoaded", function () {
  const modal1 = document.getElementById("myModal1");
  const modal2 = document.getElementById("myModal2");
  const modal3 = document.getElementById("myModal3");
  const modal4 = document.getElementById("myModal4");
  const modal5 = document.getElementById("myModal5");

  const backToModal1 = document.getElementById("backToModal1");
  const backToModal2 = document.getElementById("backToModal2");
  const backToModal3 = document.getElementById("backToModal3");
  const backToModal4 = document.getElementById("backToModal4");

  const daysValueField = document.getElementById("daysValue");

  // 진행 상황을 업데이트하는 함수
  function updateProgress(modal, step) {
    const steps = modal.querySelectorAll(".progress-question-step");
    steps.forEach((s, index) => {
      if (index <= step) {
        s.classList.add("active");
      } else {
        s.classList.remove("active");
      }
    });
  }

  // 모달을 열고 진행 상태를 업데이트하는 함수
  function showModal(modal, step, headerText) {
    modal.style.display = "block";
    modal.querySelector(".modal-header h1").textContent = headerText;
    updateProgress(modal, step);
  }

  // 첫 번째 모달 자동 열기
  showModal(modal1, 0, "여행기간은?");

  // 다음 버튼 클릭 시 이벤트 핸들러 설정
  document.querySelector("#myModal1 .button-next").onclick = function (event) {
    event.preventDefault();
    modal1.style.display = "none";
    showModal(modal2, 1, "항공권은?");
  };

  document.querySelector("#myModal2 .button-next-page2").onclick = function (event) {
    event.preventDefault();
    modal2.style.display = "none";
    showModal(modal3, 2, "누구랑?");
  };

  document.querySelector("#myModal3 .button-next-page3").onclick = function (event) {
    event.preventDefault();
    modal3.style.display = "none";
    showModal(modal4, 3, "어떻게?");
  };

  document.querySelector("#myModal4 .button-next-page4").onclick = function (event) {
    event.preventDefault();
    modal4.style.display = "none";
    showModal(modal5, 4, "일정의 밀도는?");
  };

  // 5번째 모달 제출 시 완료 메시지 표시
  document.querySelector("#myModal5 .modal-submit-button").addEventListener("click", function (event) {
    event.preventDefault();

    // first_date와 last_date 값을 숨겨진 필드에서 가져오기
    const first_date = document.getElementById("first_date")?.value;
    const last_date = document.getElementById("last_date")?.value;

    // 전송할 값들을 확인하기 위해 출력
    console.log("first_date:", first_date);
    console.log("last_date:", last_date);
    console.log("nights:", document.getElementById("nightsValue")?.value);
    console.log("days:", document.getElementById("daysValue")?.value);
    console.log("arrival:", document.getElementById("arrival")?.value);
    console.log("depart:", document.getElementById("depart")?.value);
    console.log("trip_type:", document.getElementById("trip_type")?.textContent);
    console.log("children:", document.getElementById("children")?.value);
    console.log("adult:", document.getElementById("adult")?.value);
    console.log("theme1:", document.getElementById("theme_1")?.textContent);
    console.log("theme2:", document.getElementById("theme_2")?.textContent);
    console.log("theme3:", document.getElementById("theme_3")?.textContent);
    console.log("density:", document.getElementById("density")?.value);
// density 값을 true 또는 false로 변환
    const densityValue = document.getElementById("density")?.value === "1" ? true : false;
    // 폼에서 입력한 값들을 객체로 저장
    const GptCmdDTO = {
      // 질문1
      firstDate: first_date,   // 변경
      lastDate: last_date,
      nights: document.getElementById("nightsValue")?.value,
      days: document.getElementById("daysValue")?.value,
      // 질문2
      arrival: document.getElementById("arrival")?.value,
      depart: document.getElementById("depart")?.value,
      //질문3
      tripType: document.getElementById("trip_type")?.textContent,
      children: document.getElementById("children")?.value,
      adult: document.getElementById("adult")?.value,
      // 질문4
      theme1: document.getElementById("theme_1")?.textContent,
      theme2: document.getElementById("theme_2")?.textContent,
      theme3: document.getElementById("theme_3")?.textContent,
      // 질문 5
      density: densityValue // Boolean 값으로 변환된 density
    };

    console.log("전송할 데이터:", GptCmdDTO);

    // POST 요청으로 GptCmdDTO 전송
    fetch("/gptView/saveGptCmd", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(GptCmdDTO),
    })
        .then((response) => {
          console.log("Response status:", response.status); // 응답 상태 코드 확인

          // 응답이 성공적이지 않은 경우 (200번대 이외의 상태 코드)
          if (!response.ok) {
            // 응답 본문을 파싱하여 오류 메시지 출력
            return response.text().then((text) => {
              console.error(`Server responded with status: ${response.status}, body: ${text}`);
              throw new Error(`Server responded with status: ${response.status}, body: ${text}`);
            });
          }

          // 응답이 성공적일 경우 JSON 파싱
          return response.json();
        })
        .then((data) => {
          console.log("서버 응답:", data); // 서버로부터 받은 데이터 로그 출력
          modal5.style.display = "none"; // 모달 닫기
          alert("제출이 완료되었습니다!"); // 완료 메시지 표시
        })
        .catch((error) => {
          // 상세한 오류 메시지 출력
          console.error("오류 발생:", error);
          alert(`전송 중 오류 발생: ${error.message}`);
        });
  });

    // 이전 모달로 돌아가기 버튼 클릭 시 이벤트 핸들러 설정
  backToModal1.onclick = function () {
    modal2.style.display = "none";
    showModal(modal1, 0, "여행기간은?");
  };

  backToModal2.onclick = function () {
    modal3.style.display = "none";
    showModal(modal2, 1, "항공권은?");
  };

  backToModal3.onclick = function () {
    modal4.style.display = "none";
    showModal(modal3, 2, "누구랑?");
  };

  backToModal4.onclick = function () {
    modal5.style.display = "none";
    showModal(modal4, 3, "어떻게?");
  };

  backToModal5.onclick = function () {
    modal6.style.display = "none";
    showModal(modal5, 4, "일정의 밀도는?");
  };
});
