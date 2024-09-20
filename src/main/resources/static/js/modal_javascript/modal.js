/*    모달의 전.후 페이지로 이동 DB로 전달을 담당하는 자바스크립트
*
* */

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
  /*질문1~5*/
  let qs1 = $("#qs1").text();
  let qs2 = $("#qs2").text();
  let qs3 = $("#qs3").text();
  let qs4 = $("#qs4").text();
  let qs5 = $("#qs5").text();

  showModal(modal1, 0, `${qs1}`);

  // 다음 버튼 클릭 시 이벤트 핸들러 설정
  document.querySelector("#myModal1 .button-next").onclick = function (event) {
    event.preventDefault();
    modal1.style.display = "none";
    showModal(modal2, 1, `${qs2}`);
  };

  document.querySelector("#myModal2 .button-next-page2").onclick = function (event) {
    event.preventDefault();
    modal2.style.display = "none";
    showModal(modal3, 2, `${qs3}`);
  };

  document.querySelector("#myModal3 .button-next-page3").onclick = function (event) {
    event.preventDefault();
    modal3.style.display = "none";
    showModal(modal4, 3, `${qs4}`);
  };

  document.querySelector("#myModal4 .button-next-page4").onclick = function (event) {
    event.preventDefault();
    modal4.style.display = "none";
    showModal(modal5, 4, `${qs5}`);
  };

  // 5번째 모달 제출 시 완료 메시지 표시
  document.querySelector("#myModal5 .modal-submit-button").addEventListener("click", function (event) {
    event.preventDefault();

    // 데이터를 가져오기 전에 요소가 존재하는지 확인
    const first_date = document.getElementById("first_date")?.value ;
    const last_date = document.getElementById("last_date")?.value ;

    // 동적으로 추가된 필드에서 값을 가져옴
    const startTimeElement = document.getElementById("startTime");
    const endTimeElement = document.getElementById("endTime");

    const startTimeValue = startTimeElement ? startTimeElement.value : null;
    const endTimeValue = endTimeElement ? endTimeElement.value : null;


    // 로그 확인
    console.log("arrival:", startTimeValue);
    console.log("depart:", endTimeValue);

    // 전송할 값들을 확인하기 위해 출력
    console.log("first_date:", first_date);
    console.log("last_date:", last_date);
    console.log("nights:", document.getElementById("nightsValue")?.value);
    console.log("days:", document.getElementById("daysValue")?.value);
    console.log("arrival:", startTimeValue);
    console.log("depart:", endTimeValue);
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
      firstDate: first_date,
      lastDate: last_date,
      nights: document.getElementById("nightsValue")?.value,
      days: document.getElementById("daysValue")?.value,
      arrival: startTimeValue,
      depart: endTimeValue,
      tripType: document.getElementById("trip_type")?.textContent,
      children: document.getElementById("children")?.value,
      adult: document.getElementById("adult")?.value,
      theme1: selectedThemeValues[0] ,  // 한국어 value 값 전송
      theme2: selectedThemeValues[1]  , //
      theme3: selectedThemeValues[2]  ,// 한국어 value 값 전송
      density: densityValue
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

          if (!response.ok) {
            return response.text().then((text) => {
              console.error(`Server responded with status: ${response.status}, body: ${text}`);
              throw new Error(`Server responded with status: ${response.status}, body: ${text}`);
            });
          }

          return response.json();
        })
        .then((data) => {
          console.log("서버 응답:", data);
          modal5.style.display = "none"; // 모달 닫기
          alert("제출이 완료되었습니다!");
          window.location.href = `/gptView/loading?cmdNum=${data.cmdNum}`;
        })
        .catch((error) => {
          console.error("오류 발생:", error);
          alert(`전송 중 오류 발생: ${error.message}`);
        });
  });

  // 이전 모달로 돌아가기 버튼 클릭 시 이벤트 핸들러 설정
  backToModal1.onclick = function () {
    modal2.style.display = "none";
    showModal(modal1, 0, `${qs1}`);
  };

  backToModal2.onclick = function () {
    modal3.style.display = "none";
    showModal(modal2, 1, `${qs2}`);
  };

  backToModal3.onclick = function () {
    modal4.style.display = "none";
    showModal(modal3, 2, `${qs3}`);
  };

  backToModal4.onclick = function () {
    modal5.style.display = "none";
    showModal(modal4, 3, `${qs4}`);
  };
});
