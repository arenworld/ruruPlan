document.addEventListener("DOMContentLoaded", function () {
  const modal1 = document.getElementById("myModal1");
  const modal2 = document.getElementById("myModal2");
  const modal3 = document.getElementById("myModal3");
  const modal4 = document.getElementById("myModal4");
  const modal5 = document.getElementById("myModal5");
  const modal6 = document.getElementById("myModal6");

  const backToModal1 = document.getElementById("backToModal1");
  const backToModal2 = document.getElementById("backToModal2");
  const backToModal3 = document.getElementById("backToModal3");
  const backToModal4 = document.getElementById("backToModal4");
  const backToModal5 = document.getElementById("backToModal5");

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

  document.querySelector("#myModal2 .button-next-page2").onclick = function (
    event
  ) {
    event.preventDefault();
    modal2.style.display = "none";
    showModal(modal3, 2, "누구랑?");
  };

  document.querySelector("#myModal3 .button-next-page3").onclick = function (
    event
  ) {
    event.preventDefault();
    modal3.style.display = "none";
    showModal(modal4, 3, "어떻게?");
  };

  document.querySelector("#myModal4 .button-next-page4").onclick = function (
    event
  ) {
    event.preventDefault();
    modal4.style.display = "none";
    showModal(modal5, 4, "일정의 밀도는?");
  };

  document.querySelector("#myModal5 .button-next-page5").onclick = function (
    event
  ) {
    event.preventDefault();
    modal5.style.display = "none";
    showModal(modal6, 5, "숙소는?");

    const days = daysValueField.value;
    console.log("버튼 클릭 후 days 값:", days);

    // initialDaysValue 및 remainingDays 확인
    const initialDaysValue = parseInt(days) || 0;
    const remainingDays = initialDaysValue;
    console.log("버튼 클릭 후 remainingDays 값:", remainingDays);
  };

  // 6번째 모달 제출 시 완료 메시지 표시
  document.querySelector("#myModal6 .modal-submit-button").onclick = function (
    event
  ) {
    event.preventDefault();
    modal6.style.display = "none";
    alert("정보가 성공적으로 제출되었습니다.");
    window.location.href = "/home.html"; // 원하는 페이지로 이동
  };

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

  document
    .getElementById("myModal6")
    .addEventListener("shown.bs.modal", function () {
      if (daysValueField) {
        const days = daysValueField.value;
        console.log("모달에서 days 값:", days);

        let initialDaysValue = parseInt(days) || 0;
        let remainingDays = initialDaysValue;

        console.log("모달 shown 이후 remainingDays 값:", remainingDays);
      } else {
        console.log("daysValueField를 찾을 수 없습니다.");
      }
    });
});
